package org.jeecg.modules.demo.monitor.controller.async;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.monitor.bean.DetectorData;
import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.service.IAlarmRecordService;
import org.jeecg.modules.demo.monitor.service.IAlarmSmsService;
import org.jeecg.modules.demo.monitor.service.IDeviceService;
import org.jeecg.modules.demo.monitor.service.ITestRecordService;
import org.jeecg.modules.demo.monitor.util.SattusSplitUtil;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableAsync
@Configuration
public class AsyncProcess {
	 @Autowired
    private IAlarmRecordService alarmRecordService;
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private ITestRecordService testRecordService;
    @Autowired
    private IAlarmSmsService alarmSmsService;
    @Autowired
    private RedisUtil redis;
    
    private String createKey(String imei, String iccid, String add) {
    	return new StringBuilder("DSC").append("#")
    			.append(imei).append("#")
    			.append(iccid).append("#")
    			.append(add)
    			.toString();
    }
    private  
//	    synchronized 
    void setDscStatus(String imei, String iccid, String add, Integer startAddress, char status) {
    	String key = this.createKey(imei, iccid, add);
    	Map<String, String> statusMap = (Map<String, String>)this.redis.get(key);
    	if(statusMap == null) {
    		statusMap = new HashMap<String, String>();
    	}
    	statusMap.put(String.valueOf(startAddress), String.valueOf(status));
    	this.redis.set(key, statusMap);
    	
    }
    private  
//	    synchronized 
    void setDscStatus(String imei, String iccid, String add, Map<String, String> statusMap) {
    	String key = this.createKey(imei, iccid, add);
    	this.redis.set(key, statusMap);
    	
    }
    private  
//    synchronized 
    void setDscRealDataStatus(String imei, String iccid, String add, Integer startAddress, char status) {
    	String key = "device#dcs#realTimeStatus#" + imei + iccid + add;
    	Map<String, String> statusMap = (Map<String, String>)this.redis.get(key);
    	if(statusMap == null) {
    		statusMap = new HashMap<String, String>();
    	}
    	statusMap.put(String.valueOf(startAddress), String.valueOf(status));
    	this.redis.set(key, statusMap);
    	
    }
    private 
//	    synchronized 
    String getDscStatus(String imei, String iccid, String add) {
    	String key = this.createKey(imei, iccid, add);
    	Map<String, String> statusMap = (Map<String, String>)this.redis.get(key);
    	StringBuilder status = new StringBuilder();
    	if(statusMap != null) { 
    		for(Object startAddress : statusMap.keySet()) {
    			String subStatus = this.convertStatusText(
    					statusMap.get(startAddress).charAt(0), 
    					Integer.parseInt(startAddress.toString()));
    			if(!"正常".equals(subStatus)) {
    				status.append(subStatus).append(",");
    			}
    		}
    		if(status.length() > 0) {
    			status = status.deleteCharAt(status.length() - 1);
    		}
    	} 
    	if(status.length() == 0) {
    		status.append("正常");
    	}
    	return status.toString();
    }
    private 
//    synchronized 
    String getRealTimeDscStatus(String imei, String iccid, String add) {
    	String key = "device#dcs#realTimeStatus#" + imei + iccid + add;
    	Map<String, String> statusMap = (Map<String, String>)this.redis.get(key);
    	StringBuilder status = new StringBuilder();
    	if(statusMap != null) { 
    		for(Object startAddress : statusMap.keySet()) {
    			String subStatus = this.convertStatusText(
    					statusMap.get(startAddress).charAt(0), 
    					Integer.parseInt(startAddress.toString()));
    			if(!"正常".equals(subStatus)) {
    				status.append(subStatus).append(",");
    			}
    		}
    		if(status.length() > 0) {
    			status = status.deleteCharAt(status.length() - 1);
    		}
    	} 
    	if(status.length() == 0) {
    		status.append("正常");
    	}
    	return status.toString();
    }
    /**
     * 转换为实时状态
     * @param detectorData
     * @return
     */
    public void putRealTimeData(DetectorData detectorData){ 
    	String data = detectorData.getData();
    	if(StringUtils.isEmpty(data)) {
    		return;
    	}
    	// 第一位站号、第二位命令（01：dcs状态、03：dcs协议数据）
    	String dataArray[] = data.split(" ");
    	int stationNo = this.number10By16(dataArray[0]);
    	String stationNoStr = String.valueOf(stationNo);
    	while(stationNoStr.length() < 3) {
    		// 位数不够补0
    		stationNoStr = "0" + stationNoStr;
    	}
    	int startIndex = 3;
    	if("dcs".equalsIgnoreCase(detectorData.getType()) && "01".equals(dataArray[1])) {
			this.setDcsRealTimeStatus(detectorData,
					stationNoStr, startIndex, dataArray);
    	} 
    }
    /**
     * 转换串口接口设备数据
     * @param detectorData
     * @return
     */
    public ArrayList<CaiJiVO> convertData(DetectorData detectorData, Date date){ 
    	ArrayList<CaiJiVO> caijiList = new ArrayList<CaiJiVO>();
    	String data = detectorData.getData();
    	if(StringUtils.isEmpty(data)) {
    		return caijiList;
    	}
    	if("dcs".equalsIgnoreCase(detectorData.getType())) {
			return this.convertDcsData(detectorData, date);
    	}  else if("crt".equalsIgnoreCase(detectorData.getType())) {
			return this.convertCrtData(detectorData, date);
    	}
    	return caijiList;
    }
    /**
     * 转换串口Dcs接口设备数据
     * @param detectorData
     * @return
     */
    public ArrayList<CaiJiVO> convertDcsData(DetectorData detectorData, Date date){ 
    	ArrayList<CaiJiVO> caijiList = new ArrayList<CaiJiVO>();
    	String data = detectorData.getData();
    	if(StringUtils.isEmpty(data)) {
    		return caijiList;
    	}
    	// 第一位站号、第二位命令（01：dcs状态、03：dcs协议数据）
    	String dataArray[] = data.split(" ");
    	int stationNo = this.number10By16(dataArray[0]);
    	String stationNoStr = String.valueOf(stationNo);
    	while(stationNoStr.length() < 3) {
    		// 位数不够补0
    		stationNoStr = "0" + stationNoStr;
    	}
    	int startIndex = 3;
    	// 第三位表示长度；每个设备两组16进制数据
    	int dataLength = number10By16(dataArray[2]) / 2;
    	
    	Integer driverNumber = 1;
    	for(int index = 0; index < dataLength; index++) {
    		String driverNumberStr = driverNumber.toString();
    		while(driverNumberStr.length() < 3) {
    			driverNumberStr = "0" + driverNumberStr;
    		}
    		CaiJiVO caiJiVO = new CaiJiVO();
    		caiJiVO.setType("COM");
    		caiJiVO.setImei(detectorData.getIp());
    		caiJiVO.setIccid(detectorData.getCom());
    		caiJiVO.setAdd(stationNoStr + driverNumberStr);
    		// 数据 & 状态
    		String data2bin  = this.hexStr2Byte(dataArray[startIndex]) + 
    				this.hexStr2Byte(dataArray[startIndex + 1]);
    		
			String status    = this.getRealTimeDscStatus(caiJiVO.getImei(), caiJiVO.getIccid(), caiJiVO.getAdd());//this.getDscStatus(caiJiVO.getImei(), caiJiVO.getIccid(), caiJiVO.getAdd());
			String valStr    = data2bin;
    		// 设备二进制值
    		Integer devVal   = Integer.parseInt(valStr, 2);
    		caiJiVO.setTime(date);
    		caiJiVO.setStatus(status);
    		caiJiVO.setRtStatus(caiJiVO.getStatus());
    		caiJiVO.setPv(devVal.toString());
    		
    		driverNumber++;
    		startIndex += 2;
    		
    		caijiList.add(caiJiVO);
    	}
    	return caijiList;
    }
    /**
     * 转换串口Crt接口设备数据
     * @param detectorData
     * @return
     */
    public ArrayList<CaiJiVO> convertCrtData(DetectorData detectorData, Date date){ 
    	ArrayList<CaiJiVO> caijiList = new ArrayList<CaiJiVO>();
    	String data = detectorData.getData();
    	if(StringUtils.isEmpty(data)) {
    		return caijiList;
    	}
    	// 第一位站号、第二位命令（01：dcs状态、03：dcs协议数据）
    	String dataArray[] = data.split(" ");
    	int stationNo = this.number10By16(dataArray[0]);
    	String stationNoStr = String.valueOf(stationNo);
    	while(stationNoStr.length() < 3) {
    		// 位数不够补0
    		stationNoStr = "0" + stationNoStr;
    	}
    	int startIndex = 3;
    	// 第三位表示长度；每个设备两组16进制数据
//    	int dataLength = number10By16(dataArray[2]) / 2;
    	// 2020-06-20 调整为通过数据动态识别长度
    	int dataLength = (dataArray.length - 5) / 2;
    	
    	Integer driverNumber = 1;
    	for(int index = 0; index < dataLength; index++) {
    		String driverNumberStr = driverNumber.toString();
    		while(driverNumberStr.length() < 3) {
    			driverNumberStr = "0" + driverNumberStr;
    		}
    		if("126".equals(driverNumberStr)) {
    			System.out.println(driverNumberStr);
    		}
    		CaiJiVO caiJiVO = new CaiJiVO();
    		caiJiVO.setType("COM");
    		caiJiVO.setImei(detectorData.getIp());
    		caiJiVO.setIccid(detectorData.getCom());
    		caiJiVO.setAdd(stationNoStr + driverNumberStr);
    		// 数据 & 状态
    		String data2bin  = this.hexStr2Byte(dataArray[startIndex]) + 
    				this.hexStr2Byte(dataArray[startIndex + 1]);
    		
			String alarmStr  = data2bin.substring(0, 4);
			caiJiVO.setStatusBit(alarmStr);
			String status = convertToStatus(alarmStr);
			
			String valStr    = data2bin.substring(4);
    		// 设备二进制值
    		Integer devVal   = Integer.parseInt(valStr, 2);
    		caiJiVO.setTime(date);
    		caiJiVO.setStatus(status);
    		caiJiVO.setRtStatus(caiJiVO.getStatus());
    		caiJiVO.setPv(devVal.toString());
    		
    		driverNumber++;
    		startIndex += 2;
    		
    		caijiList.add(caiJiVO);
    	}
    	return caijiList;
    }
    private Object getStationLastDataTime(String key) {
    	return this.redis.get("device#lastDataTime#station#" + key);
    }
    /**
     * dcs 协议心跳兼容
     * @param reqData
     * @param startIndex
     * @param dataArray
     */
    private 
//	    synchronized 
    void saveDcsHeatData(DetectorData reqData, 
    		String stationNoStr, int startIndex, String[] dataArray) {
    	Date now = new Date();
    	int dataLength = this.number10By16(dataArray[2]);
    	List<CaiJiVO> cvs = new ArrayList<CaiJiVO>();
    	for(int dataIndex = 0; dataIndex < dataLength; dataIndex++) {
    		String statusStr   = dataArray[startIndex + dataIndex];
    		String statusbin2  = this.hexStr2Byte(statusStr);
    		char   bintArray[] = statusbin2.toCharArray();
    		int groupIndex = (dataIndex + 1) * bintArray.length;
    		for(char ba : bintArray) {
    			String addressNumer = String.valueOf(groupIndex);
    			while(addressNumer.length() < 3) {
    				addressNumer = "0" + addressNumer;
    			}
    			groupIndex --;
    			// 查设备
                CaiJiVO caiJiVO = new CaiJiVO();
                caiJiVO.setImei(reqData.getIp());
                caiJiVO.setIccid(reqData.getCom());
    			caiJiVO.setAdd(stationNoStr + addressNumer);
                caiJiVO.setType("COM");
//                if(caiJiVO.getAdd().equals("002001")) {
//                	System.out.println("");
//                }
    			String key = this.createKey(caiJiVO.getImei(), caiJiVO.getIccid(), 
    					caiJiVO.getAdd());
    			// 上次的状态
    			Map<String, String> preStatusMap = (Map<String, String>)this.redis.get(key);
    			// 设置最新状态
    			this.setDscStatus(caiJiVO.getImei(), caiJiVO.getIccid(), 
    					caiJiVO.getAdd(), reqData.getStartAddress(), ba);
    			// 得到当前状态
    			Map<String, String> curStatusMap = (Map<String, String>)this.redis.get(key);
    			// 对比得到解除状态
    			String preStatusText  = this.getDiffAlarmStatusDcs(preStatusMap, curStatusMap);
    			boolean vlidateAlarmStatus = this.vlidateAlarmStatusDcs(preStatusMap, curStatusMap);
    			
    			String statusText = this.getDscStatus(caiJiVO.getImei(), caiJiVO.getIccid(), caiJiVO.getAdd());
    			if(!"正常".equals(statusText)) {
    				log.info("异常设备：" + caiJiVO.getImei() + "#" + caiJiVO.getIccid()+ "#" + caiJiVO.getAdd() + "：" + statusText);
    			}
    			
//	    			if(ba == '1') { // 命中：异常报警
//	    				// 更新状态
//	    				statusText = this.getDscStatus(caiJiVO.getImei(), caiJiVO.getIccid(), caiJiVO.getAdd());
//	    				this.saveAlarmRecord(device, preStatusText, statusText, now, "#A", false);
//	    			} 
    			Date date = new Date();
    			Device device = this.deviceService.findDeviceByImeiAndAdd(caiJiVO);
    			this.logger("查找设备耗时：", date);// + (new Date().getTime() - date.getTime() ) + "毫秒");
    			if(device == null) {
//	    				this.logger("未找到设备" + JSON.toJSON(caiJiVO));
    				continue;
    			}
    			date = new Date();
    			CaiJiVO cv = this.saveAlarmRecord(device, preStatusText, statusText, now, "#A", false, vlidateAlarmStatus);
    			if(cv != null) {
    				cvs.add(cv);
    			}
    			this.logger("存入报警耗时：" , date);//+ (new Date().getTime() - date.getTime() ) + "毫秒");

//				device.setStatusType(statusText);
//    	    	// 更新心跳数据
//        		device.setBackup3(dateFormat.format(now));
//        		date = new Date();
//        		this.deviceService.updateHeatTimeStatus(device);
//    			this.logger("更新设备状态耗时：", date);// + (new Date().getTime() - date.getTime() ) + "毫秒");
    		}
    	}
    	if(cvs.size() > 0) {
    		this.deviceService.syncRealDataById(cvs);
    	}
//	    	deviceService.updateBatchById(entityList);
	}
    /**
     * dcs 协议心跳兼容
     * @param reqData
     * @param startIndex
     * @param dataArray
     */
    private 
    void setDcsRealTimeStatus(DetectorData reqData, 
    		String stationNoStr, int startIndex, String[] dataArray) {
    	int dataLength = this.number10By16(dataArray[2]);
    	for(int dataIndex = 0; dataIndex < dataLength; dataIndex++) {
    		String statusStr   = dataArray[startIndex + dataIndex];
    		String statusbin2  = this.hexStr2Byte(statusStr);
    		char   bintArray[] = statusbin2.toCharArray();
    		int groupIndex = (dataIndex + 1) * bintArray.length;
    		for(char ba : bintArray) {
    			String addressNumer = String.valueOf(groupIndex);
    			while(addressNumer.length() < 3) {
    				addressNumer = "0" + addressNumer;
    			}
    			groupIndex --;
    			String add = stationNoStr + addressNumer;
    			// 设置最新状态
    			this.setDscRealDataStatus(reqData.getIp(), reqData.getCom(), 
    					add, reqData.getStartAddress(), ba);
    		}
    	}
//	    	deviceService.updateBatchById(entityList);
    }
    /**
     * @param b
     * @param startAddress
     * @return
     */
    public String convertStatusText(char b, int startAddress) {
    	if(b == '1') {
	    	if(startAddress == 0) {
//		    		return "屏蔽";
	    		return "维护";
			} else if(startAddress == 200) {
	    		return "故障";
			} else if(startAddress == 400) {
	    		return "低警";
			} else if(startAddress == 600) {
	    		return "高警";
			} 
    	} 
    	return "正常";
    }
	/**
     * @param alarmStr
     * @return
     */
    public String convertToStatus(String alarmStr){
        //判断一下：如果转化为二进制为0或者1或者不满8位，要在数后补0
        char[] chars = alarmStr.toCharArray();
        String ss ="";
        if(chars[3]=='1'){
//	            ss += "屏蔽,";
            ss += "维护,";
        }
        if(chars[2]=='1'){
            ss += "故障,";
        }
        if(chars[1]=='1'){
            ss += "高警,";
        }
        if(chars[0]=='1'){
            ss += "低警,";
        } 
        if(ss.equals("")){
            ss = "正常";
        }else{
            ss = ss.substring(0, ss.length() - 1);
        }
        return ss;
    }
    /**
     * 16进制转2进制
     * @param hex
     * @return
     */
    public String hexStr2Byte(String hex) {
       //字符串形式的:16进制!
        //字符串形式十进制--作为桥梁!
        int sint=Integer.valueOf(hex, 16);
        //十进制在转换成二进制的字符串形式输出!
        String bin=Integer.toBinaryString(sint);
        while(bin.length() < 8) {
        	bin = "0" + bin;
        }
        return bin;
    }
    /**
     * 16 进制转10进制
     * @return
     */
    private int number10By16(String number) {
    	return Integer.valueOf(number, 16);
    }
    /**
     * 异步处理请求
     * @param caiJiVOs
     * @param dataType
     */
    @Async("simpleThreadPool")
    public void asyncPutComData(List<DetectorData> dataList, long time){
    	if("DCS".equalsIgnoreCase(dataList.get(0).getType())) {
    		int a = 0;
    		// 将dcs4个状态更新至redis
    		for(DetectorData data : dataList) {
    			if(a > 0) {
    				this.putRealTimeData(data);
    			}
    			a++;
    		}
    		this.asyncPutComData(dataList.get(0), time);
    	} else {
	    	for(DetectorData data : dataList) {
	    		this.asyncPutComData(data, time);
	    	}
    	}
    }
    /**
     * 异步处理请求
     * @param caiJiVOs
     * @param dataType
     */
//    @Async("simpleThreadPool")
    public void asyncPutComData(DetectorData data, long time){
    	Date sd = new Date(time);
        this.logger("异步方法时差" + JSON.toJSONString(data), sd);
    	Date start = new Date();
    	ArrayList<CaiJiVO> caiJiVOs = this.convertData(data, sd);
        this.logger("解析[" + JSON.toJSONString(data) + "]数据耗时：", start);// + (new Date().getTime() - start.getTime())  + " 毫秒；传入到解析完成耗时：" + (new Date().getTime() - sd.getTime()) + "毫秒；" + data.getData());
    	if(caiJiVOs == null || caiJiVOs.size() == 0) {
    		return;
    	}
//			System.out.println("time===============>");
    	//至此已获得实时数据集合
    	for(CaiJiVO caiJiVO : caiJiVOs){
    		this.setDevice(caiJiVO);
    	}
    	start = new Date();
        this.logger("绑定设备耗时：", start);// + (new Date().getTime() - sd.getTime()) + "毫秒");
        this.asyncAlarm(caiJiVOs, data.getType());
        this.asyncSaveRecord(caiJiVOs);
//    	applicationContext.getBean(ComRecordController.class).asyncSaveRecord(caiJiVOs);
//    	applicationContext.getBean(ComRecordController.class).asyncAlarm(caiJiVOs, data.getType());
    	
        //this.logger("单站点处理累计耗时：", sd);// + (new Date().getTime() - sd.getTime()) + "毫秒");
    }
    /**
     * 异步处理请求
     * @param caiJiVOs
     * @param dataType
     */
//    @Async
    public void asyncSaveRecord(List<CaiJiVO> caiJiVOs){
    	if(caiJiVOs == null || caiJiVOs.size() == 0) {
    		return;
    	}
    	Date sd = new Date();
    	List<CaiJiVO> cjVoList = new ArrayList<CaiJiVO>();
    	for(CaiJiVO caiJiVO : caiJiVOs){
//	    		System.out.println("time2:" + dateFormat.format(caiJiVO.getTime()));
    		if(caiJiVO.getDevice() == null) {
    			continue;
    		}
    		// 判断是否为输出模块，设置输出模块状态变化值
        	if("3".equals(caiJiVO.getDevice().getDeviceType())) {
        		if(!"0".equals(caiJiVO.getPv())) {
        			caiJiVO.setOutStatus("已动作");
        		} else {
        			caiJiVO.setOutStatus("动作取消");
        		}
        	}
    		cjVoList.add(caiJiVO);
    	}
    	if(cjVoList.size() > 0) {
    		this.testRecordService.addTest(cjVoList);
    		this.deviceService.syncRealDataById(cjVoList);
    		this.logger("保存采集数据耗时：", sd);// + (new Date().getTime() - sd.getTime()) + "毫秒");
    	}
    }
    /**
     * 异步处理请求
     * @param caiJiVOs
     * @param dataType
     */
//    @Async
    public void asyncAlarm(List<CaiJiVO> caiJiVOs, String type){
    	if(caiJiVOs == null || caiJiVOs.size() == 0) {
    		return;
    	}
    	List<CaiJiVO> cvs = new ArrayList<CaiJiVO>();
		Date sd = new Date();
    	for(CaiJiVO caiJiVO : caiJiVOs){
    		if(caiJiVO.getDevice() == null) {
    			continue;
    		}
			Device device = caiJiVO.getDevice();
			String preStatus = null;
			boolean vlidateAlarmStatus = true;
			if("crt".equalsIgnoreCase(type)){
				preStatus = this.getDiffAlarmStatusCrt(
    					(String)this.redis.get("tcp#device#" + device.getId()), 
    					caiJiVO.getStatusBit());
			} else {
    			String preKey = this.createKey(caiJiVO.getImei(), caiJiVO.getIccid(), 
    					caiJiVO.getAdd());
				String key = "device#dcs#realTimeStatus#" + caiJiVO.getImei() + 
						caiJiVO.getIccid() + caiJiVO.getAdd();
		    	
				// 上次的状态
    			Map<String, String> preStatusMap = (Map<String, String>)this.redis.get(preKey);
    			// 得到当前状态
    			Map<String, String> curStatusMap = (Map<String, String>)this.redis.get(key);
    			// 替换为最新状态
    			this.setDscStatus(caiJiVO.getImei(), caiJiVO.getIccid(), 
    					caiJiVO.getAdd(), curStatusMap);
    			// 对比得到解除状态
    			preStatus  = this.getDiffAlarmStatusDcs(preStatusMap, curStatusMap);
    			vlidateAlarmStatus = this.vlidateAlarmStatusDcs(preStatusMap, curStatusMap);
			}
    		device.setBackup2(caiJiVO.getPv());
    		device.setBackup4(caiJiVO.getStatusBit());
    		CaiJiVO cv = this.saveAlarmRecord(device, preStatus, caiJiVO.getStatus(), caiJiVO.getTime(), "#M", true, vlidateAlarmStatus);
    		if(cv != null) {
    			cvs.add(cv);
    		}
    		this.redis.set("tcp#device#" + device.getId(), caiJiVO.getStatusBit());
    	}
    	this.logger("验证并报警耗时：", sd);// + (new Date().getTime() - sd.getTime()) + "毫秒");
    	if(cvs.size() > 0) {
    		this.deviceService.syncRealDataById(cvs);
    	}
    }
    private boolean setDevice(CaiJiVO caiJiVO) {
    	Device device = deviceService.findDeviceByImeiAndAdd(caiJiVO);//获得对应设备
        if(device == null) {
//	        	this.logger("未找到设备：" + JSON.toJSONString(caiJiVO));
        	return false;
        } 
        caiJiVO.setDevice(device);
        
        caiJiVO.setDeviceId(device.getId());
        caiJiVO.setCustomerId(device.getCustomerId());
        caiJiVO.setOneAreaId(device.getOneAreaId());
        caiJiVO.setTwoAreaId(device.getTwoAreaId());
        return true;
    }
    
    /**
     * 获取COM DCS设备恢复状态
     * @param preStatus
     * @param curStatus
     * @return
     */
    private String getDiffAlarmStatusDcs(Map<String, String> preStatus, Map<String, String> curStatus) {
    	if(preStatus == null) {
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	// 解除某个报警
    	if("1".equals(preStatus.get("0")) && "0".equals(curStatus.get("0"))) {
    		sb.append("解除维护").append(",");
//				sb.append("解除屏蔽").append(",");
    	}
    	if("1".equals(preStatus.get("200")) && "0".equals(curStatus.get("200"))) {
			sb.append("解除故障").append(",");
    	}
    	if("1".equals(preStatus.get("400")) && "0".equals(curStatus.get("400"))) {
			sb.append("解除低警").append(",");
    	}
    	if("1".equals(preStatus.get("600")) && "0".equals(curStatus.get("600"))) {
			sb.append("解除高警").append(",");
    	}
    	if(sb.length() > 0) {
    		sb = sb.deleteCharAt(sb.length() - 1);
    	}
    	return sb.toString();
    }
    /**
     * 验证是否保存报警表
     * @param preStatus
     * @param curStatus
     * @return
     */
    private boolean vlidateAlarmStatusDcs(Map<String, String> preStatus, Map<String, String> curStatus) {
    	if(preStatus == null) {
    		return true;
    	}
    	if("0".equals(preStatus.get("0")) && "1".equals(curStatus.get("0"))) {
    		return true;
    	}
    	if("0".equals(preStatus.get("200")) && "1".equals(curStatus.get("200"))) {
    		return true;
    	}
    	if("0".equals(preStatus.get("400")) && "1".equals(curStatus.get("400"))) {
    		return true;
    	}
    	if("0".equals(preStatus.get("600")) && "1".equals(curStatus.get("600"))) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * 获取CRT设备恢复状态
     * @param preStatus
     * @param curStatus
     * @return
     */
    private String getDiffAlarmStatusCrt(String preStatus, String curStatus) {
    	if(StringUtils.isEmpty(preStatus)) {
    		return null;
    	}
    	char[]  curCharStatus = curStatus.toCharArray();
    	char[]  preCharStatus = preStatus.toCharArray();
    	StringBuilder sb = new StringBuilder();
    	String recoveryStatus[]  = new String[] {
    			"解除低警", 
    			"解除高警", 
    			"解除故障", 
//	    			"解除屏蔽",
    			"解除维护"
    			};
    	for(int a = 0; a < curCharStatus.length; a++) {
    		// 解除某个报警
    		if(preCharStatus[a] == '1' && curCharStatus[a] == '0') {
    			sb.append(recoveryStatus[a]).append(",");
    		}
    	}
    	if(sb.length() > 0) {
    		sb = sb.deleteCharAt(sb.length() - 1);
    	}
    	return sb.toString();
    }
    /**
     * 获取4G设备恢复状态
     * @param preStatus
     * @param curStatus
     * @return
     */
    private String getDiffAlarmStatus4G(String preStatus, String curStatus) {
    	if(StringUtils.isEmpty(preStatus)) {
    		return null;
    	}
    	char[]  curCharStatus = curStatus.toCharArray();
    	char[]  preCharStatus = preStatus.toCharArray();
    	StringBuilder sb = new StringBuilder();
    	String recoveryStatus[]  = new String[] {"解除维护", "解除备电欠压", 
    			"解除主电欠压", "解除高警", "解除低警", "解除故障"};
    	for(int a = 0; a < curCharStatus.length; a++) {
    		// 解除某个报警
    		if(preCharStatus[a] == '1' && curCharStatus[a] == '0') {
    			sb.append(recoveryStatus[a]).append(",");
    		}
    	}
    	if(sb.length() > 0) {
    		sb = sb.deleteCharAt(sb.length() - 1);
    	}
    	return sb.toString();
    }
//	    /**
//	     * 设置设备的实时状态
//	     */
//	    private void setDeviceRealTimeStatus(Integer deviceId, String statusType, String heatTime) {
//	    	String key = "deviceRealTimeStatue" + deviceId;
//	    	this.redis.set(key, new String[] {statusType, heatTime});
//	    }
//	    private String[] getDeviceRealTimeStatus(Integer deviceId) {
//	    	String key = "deviceRealTimeStatue" + deviceId;
//	    	return (String[])this.redis.get(key);
//	    }

    /**
     * 添加设备报警
     * @param device
     * @param date
     */
    private 
//	    synchronized 
    CaiJiVO saveAlarmRecord(Device device, final String preStatusText,
    		String statusText, Date date, String flag, boolean saveRecord, boolean vlidateAlarmStatus) {
    	try {
			Date logDate    = new Date();
    		Device dbDevice = this.deviceService.getById(device.getId());
    		this.logger("读取设备实时状态耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
    		
    		String dbStatusType = dbDevice.getStatusType();
    		device.setStatusType(dbStatusType);

    		// 04-22 不显示上一次报警文本
    		String placeText = statusText;
    		if(StringUtils.isNotEmpty(dbStatusType) 
    				&& StringUtils.isNoneEmpty(statusText)) {
    			String[] statusTextArray = statusText.split(",");
    			StringBuilder notPlaceText = new StringBuilder();
    			for(String s : statusTextArray) {
    				if(dbStatusType.indexOf(s) == -1) { // 当前状态不存在于上一次状态时添加
    					notPlaceText.append(s).append(",");
    				}
    			}
    			if(notPlaceText.length() > 0) {
    				statusText = notPlaceText.deleteCharAt(notPlaceText.length() - 1).toString();
    			}
    		}
    		CaiJiVO caiJiVO = new CaiJiVO();
    		caiJiVO.setStatusBit(device.getBackup4());
            caiJiVO.setStatus(statusText);
	        caiJiVO.setRtStatus(caiJiVO.getStatus());
            caiJiVO.setTime(date);
            caiJiVO.setImei(device.getDeviceImei());
            caiJiVO.setAdd(device.getAddressNumber());
            caiJiVO.setIccid(device.getIccid());
            caiJiVO.setDeviceId(device.getId());
            caiJiVO.setPv(device.getBackup2());
            
//	            String lastDataTime = (String)this.redis.get("device#lastDataTime#" 
//		   				 + caiJiVO.getType() + "#"
//		       			 + caiJiVO.getImei() + "#"
//						 + caiJiVO.getIccid() + "#"
//						 + caiJiVO.getAdd());
            String lastDataTime = (String)this.getStationLastDataTime(caiJiVO.getAdd().substring(0,  3));
//	    		String dataTime = device.getBackup1() + "#" + device.getBackup3() + flag;
    		String dataTime = lastDataTime + "#" + dbDevice.getBackup3() + flag;
    		AlarmRecord alarmRecord = convertAlarmRecord(caiJiVO, device.getDeviceType(), dataTime);
			alarmRecord.setBackup2(device.getId());
			alarmRecord.setBackup3(caiJiVO.getStatusBit());
			logDate = new Date();
			// 查询是否处于通讯故障
            Map<String, Object> idMap = this.alarmRecordService.selectConnectionError(caiJiVO);
            this.logger("查询是否有通讯故障耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
            if(idMap != null && idMap.size() > 0) {
            	logDate = new Date();
            	// 标记为通讯恢复
            	this.alarmRecordService.updateConnectionRecovery(caiJiVO);
            	this.logger("标记通讯恢复耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
            	alarmRecord.setAlarmState(1);
            	alarmRecord.setTstatus(this.getRecordStatus(dbDevice, caiJiVO.getPv(), "通讯恢复"));
            	logDate = new Date();
            	alarmRecordService.insertAlarm(alarmRecord);
            	this.logger("插入通讯恢复耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
            }
            // 同步数据库状态时，保留值
	        caiJiVO.setRtStatus(placeText);
            logDate = new Date();
			this.logger("同步设备状态耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
			// 对比上一次数据状态，有变化先恢复
			if(StringUtils.isNotEmpty(preStatusText)) {
				String alarmRecordKey = "alarmRecord#" + device.getId();
	            Object alarmVal = redis.get(alarmRecordKey);
	            // 避免同一设备同一类型重复解除
	            if(alarmVal == null || !alarmVal.toString().equals(preStatusText + "#" + dataTime)) {
	            	alarmRecord.setAlarmState(1);
	            	
	            	alarmRecord.setTstatus(this.getRecordStatus(dbDevice, caiJiVO.getPv(), preStatusText));
	            	
					logDate = new Date();
	            	alarmRecordService.insertAlarm(alarmRecord);
	            	redis.set(alarmRecordKey, preStatusText + "#" + dataTime);
	            	
	            	// 恢复报警移除redis
	    			String alarmKey = "alarm#" + device.getId();
	    			redis.del(alarmKey);
	    			this.logger("插入解除报警耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
	            }
			}
			if("正常".equals(device.getStatusType())) { // 设备恢复正常，重置报警位
				String alarmKey = "alarm#" + device.getId();
    			redis.del(alarmKey);
				String alarmRecordKey = "alarmRecord#" + device.getId();
    			redis.del(alarmRecordKey);
			}
    		// 监控状态=设备状态 不重复报警
    		if(statusText.equals(device.getStatusType()) || !vlidateAlarmStatus){
    			if(!saveRecord) {
                	caiJiVO.setTime(null);
    			}
    			return caiJiVO;
    		}
            
            caiJiVO.setCustomerId(device.getCustomerId());
            caiJiVO.setDataTime(dataTime);
            caiJiVO.setStatusBit(device.getBackup4());
            
            if(device.getBackup2() != null){
                caiJiVO.setPv("0");
            }
            // 2020-04-29 取消报警存采集表 
//            if(!saveRecord) {
//            	//非数据添加时，添加监测记录表：报警
//            	List<CaiJiVO> caijiList = new ArrayList<CaiJiVO>();
//            	caijiList.add(caiJiVO);
//            	
//				logDate = new Date();
//            	testRecordService.addTest(caijiList);
//            	this.logger("报警插入采集耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
//            }
            if(!"正常".equals(statusText)) {
    			String alarmKey = "alarm#" + device.getId();
                Object alarmVal = redis.get(alarmKey);
                // 避免同一设备类型重复报警
                if(alarmVal == null || !alarmVal.toString().equals(statusText)) {
    				logDate = new Date();
                	alarmRecord.setAlarmState(0);
                	alarmRecord.setTstatus(SattusSplitUtil.getStatus1(caiJiVO));
                    //添加报警表
                    alarmRecordService.insertAlarm(alarmRecord);
                    this.logger("插入报警耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
                    //添加报警短信表
                    logDate = new Date();
                    alarmSmsService.insertSms(alarmRecord);
                    redis.set(alarmKey, statusText);
                    this.logger("插入报警短信耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
                }
            }
            //更新设备表
            if(!saveRecord) {
            	caiJiVO.setTime(null);
				logDate = new Date();
//            	deviceService.syncRealDataById(caiJiVO);
            	this.logger("同步设备状态耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");
            }
            return caiJiVO;
        } catch (Exception e) {
        	log.error("添加报警异常", e);
        }
    	return null;
    }
    private String getRecordStatus(Device device, String pv, String preStatus) {
    	// 如果为输出模块需加上pv变化记录
		if("3".equals(device.getDeviceType())) {
			if(StringUtils.isNotBlank(pv)) {
				try {
					if(Integer.parseInt(pv) == 0) {
						preStatus += "（未动作）";
					} else {
						preStatus += "（已动作）";
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return preStatus;
    }
    private AlarmRecord convertAlarmRecord(CaiJiVO caiJiVO, String deviceType, String flag) {
    	AlarmRecord alarmRecord = new AlarmRecord();
        alarmRecord.setAlarmDeviceImei(caiJiVO.getImei());
        alarmRecord.setAlarmICCID(caiJiVO.getIccid());
        alarmRecord.setAlarmDeviceType(deviceType);
        alarmRecord.setAlarmAddress(caiJiVO.getAdd());
        alarmRecord.setBackup5(flag);
        if(caiJiVO.getPv() != null){
            alarmRecord.setAlarmPv(new BigDecimal(caiJiVO.getPv()));
        }
        alarmRecord.setAlarmTime(caiJiVO.getTime());
        //添加报警记录
        alarmRecord.setAlarmState(0);
        alarmRecord.setTstatus(SattusSplitUtil.getStatus1(caiJiVO));
        return alarmRecord;
    }

    public String numStatus(CaiJiVO caiji){
    	String str = caiji.getStatus();
        Integer num = Integer.valueOf(str);
        String tempStr = Integer.toBinaryString(num);
        //判断一下：如果转化为二进制为0或者1或者不满8位，要在数后补0
        while(tempStr.length() < 6) {
        	tempStr = "0" + tempStr;
        }
        caiji.setStatusBit(tempStr);
        char[] chars = tempStr.toCharArray();
        String ss ="";
        if(chars[5]=='1'){
            ss += "故障,";
        }
        if(chars[4]=='1'){
            ss += "低警,";
        }
        if(chars[3]=='1'){
            ss += "高警,";
        }
        if(chars[2]=='1'){
            ss += "主电欠压,";
        }
        if(chars[1]=='1'){
            ss += "备电欠压,";
        }
        if(chars[0]=='1'){
            ss += "维护,";
        }

        if(ss.equals("")){
            ss = "正常";
        }else{
            ss = ss.substring(0,ss.length()-1);
        }
        return ss;
    }
    private void logger(String text, Date preDate) {
    	Date now = new Date();
    	long ex = now.getTime() - preDate.getTime();
    	log.info(text + ex + "毫秒 " 
    			+ (ex > 10000 ? "大于10秒" : "")
    			+ (ex > 60 * 1000 ? "大于1分钟" : "")
    			+ (ex > 2 * 60 * 1000 ? "大于2分钟" : "")
    			+ (ex > 3 * 60 * 1000 ? "大于3分钟" : "")
    			+ (ex > 4 * 60 * 1000 ? "大于4分钟" : "")
    			+ (ex > 5 * 60 * 1000 ? "大于5分钟" : "")
    			+ (ex > 6 * 60 * 1000 ? "大于6分钟" : "")
    			+ (ex > 7 * 60 * 1000 ? "大于7分钟" : "")
    			+ (ex > 8 * 60 * 1000 ? "大于8分钟" : "")
    			+ (ex > 9 * 60 * 1000 ? "大于9分钟" : "")
    			+ (ex > 10 * 60 * 1000 ? "大于10分钟" : "")
    			);
    }
}
