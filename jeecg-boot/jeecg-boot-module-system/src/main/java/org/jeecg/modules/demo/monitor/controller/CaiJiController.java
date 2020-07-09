package org.jeecg.modules.demo.monitor.controller;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.service.IAlarmRecordService;
import org.jeecg.modules.demo.monitor.service.IAlarmSmsService;
import org.jeecg.modules.demo.monitor.service.IDeviceService;
import org.jeecg.modules.demo.monitor.service.ITestRecordService;
import org.jeecg.modules.demo.monitor.util.SattusSplitUtil;
import org.jeecg.modules.demo.monitor.vo.CaiJiStrVO;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "采集接口")
@RestController
@EnableAsync
@RequestMapping("/monitor/caiji")
public class CaiJiController {
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
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 按设备imei 判断是否报警
     * @param imei 
     */
    private void setImmeiDeviceHeat(String imei) {
    	Date now = new Date();
    	redis.set("imei#" + imei, now);
    	CaiJiVO cv = new CaiJiVO();
    	cv.setImei(imei);
    	deviceService.updateHeatTime(cv);
    }
    /**
     * 转换4G 设备数据
     * @param dataStr
     * @return
     */
    private ArrayList<CaiJiVO> convertData(String dataStr){
        Date date = new Date();
    	ArrayList<CaiJiVO> caiJiVOs = new ArrayList<CaiJiVO>();
    	 String[] split = dataStr.split(";");
         List<String> strs = Arrays.asList(split);
         String imei = strs.get(1).split("=")[1];
         if(strs.size() < 3) { // 仅imei即表示设备心跳数据
        	 this.setImmeiDeviceHeat(imei);
        	 return null;
         }
         String iccid = strs.get(2).split("=")[1];
         String ss2 = "";
         for (int i = 4; i < split.length; i++) {
             if(split[i].split("=")[0].equals("ADD")&&i!=4){
                 ss2 += "|"+split[i]+";";
             }else{
                 ss2 += split[i]+";";
             }
         }
         String[] split1 = ss2.split("\\|");
         for (int i = 0; i < split1.length; i++) {
             CaiJiVO caiJiVO = new CaiJiVO();
             caiJiVO.setImei(imei);
             caiJiVO.setIccid(iccid);
             caiJiVO.setTime(date);
             caiJiVO.setType("4G");
             String[] device2 = split1[i].split(";");
             caiJiVO.setAdd(device2[0].split("=")[1]);
             caiJiVO.setStatus(device2[1].split("=")[1]);
             String status = numStatus(caiJiVO);
             caiJiVO.setStatus(status);
	         caiJiVO.setRtStatus(caiJiVO.getStatus());
             if(device2.length>2){
                 caiJiVO.setPv(device2[2].split("=")[1]);
             }
             // 未找到设备不添加
        	if(!this.setDevice(caiJiVO)) {
        		continue;
        	}
        	// 立即更新设备数据时间，避免数据定时任务数据检测延迟导致报通讯故障
        	this.setStationLastDataTime(imei, dateFormat.format(date));
        	// 判断是否为输出模块，设置输出模块状态变化值
        	if("3".equals(caiJiVO.getDevice().getDeviceType())) {
        		if(!"0".equals(caiJiVO.getPv())) {
        			caiJiVO.setOutStatus("已动作");
        		} else {
        			caiJiVO.setOutStatus("动作取消");
        		}
        	}
             caiJiVOs.add(caiJiVO);
         }
         return caiJiVOs;
    }
    private Object getStationLastDataTime(String key) {
    	return this.redis.get("device#lastDataTime#station#" + key);
    }
    private Object setStationLastDataTime(String key, Object date) {
    	return this.redis.set("device#lastDataTime#station#" + key, date);
    }
    /**
     * @param b
     * @param startAddress
     * @return
     */
    public String convertStatusText(char b, int startAddress) {
    	if(b == '1') {
	    	if(startAddress == 0) {
//	    		return "屏蔽";
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
//            ss += "屏蔽,";
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
    
    
    //@AutoLog(value = "采集数据-添加")
    @ApiOperation(value = "服务采集数据-添加", notes = "服务采集数据-添加")
    @PostMapping(value = "/queryAlarmRecordById")
    public Result<?> CaiJiShuJuTianJiao(@RequestBody CaiJiStrVO caiJiStrVO){
//    	log.error("开始采集4G设备数据：" + caiJiStrVO.getCaiJi());
//        caiJiShuJu = "NO=42;IMEI=866262045791299;ICCID=89860411101893280735;SS=31;ADD=1;STATUS=3;ADD=1001;STATUS=2;PV=28;ADD=1002;STATUS=0;PV=0;ADD=1003;STATUS=0;PV=0;ADD=1004;STATUS=1;PV=0;ADD=2;STATUS=1;ADD=2001;STATUS=1;PV=0;ADD=2002;STATUS=1;PV=0;ADD=2003;STATUS=1;PV=0;ADD=2004;STATUS=1;PV=0;";
    	ArrayList<CaiJiVO> caiJiVOs = this.convertData(caiJiStrVO.getCaiJi());
    	if(caiJiVOs == null || caiJiVOs.size() == 0) {
//    		log.error("收到心跳" + caiJiStrVO.getCaiJi());
    		return Result.ok("心跳接收成功");
    	} else {
//    		log.error("收到数据" + caiJiStrVO.getCaiJi());
    	}
    	this.testRecordService.addTest(caiJiVOs);
    	List<CaiJiVO> cvs = new ArrayList<CaiJiVO>();
    	//至此已获得实时数据集合
        for(CaiJiVO caiJiVO : caiJiVOs){
            Device device = caiJiVO.getDevice();
            String preStatus = this.getDiffAlarmStatus4G(
            		(String)this.redis.get("tcp#device#" + device.getId()), 
        			caiJiVO.getStatusBit());
            device.setBackup2(caiJiVO.getPv());
            device.setBackup4(caiJiVO.getStatusBit());
            
            CaiJiVO cv = this.saveAlarmRecord(device, preStatus, caiJiVO.getStatus(), caiJiVO.getTime(), "#M", true);
            if(cv != null) {
            	cvs.add(cv);
            }
            this.redis.set("tcp#device#" + device.getId(), caiJiVO.getStatusBit());
        }
        if(cvs.size() > 0) {
        	this.deviceService.syncRealDataById(cvs);
        }
    	return Result.ok("添加成功");
    }
    /**
     * 异步处理请求
     * @param caiJiVOs
     * @param dataType
     */
    public void asyncSaveRecord(List<CaiJiVO> caiJiVOs){
    	if(caiJiVOs == null || caiJiVOs.size() == 0) {
    		return;
    	}
    	Date sd = new Date();
    	List<CaiJiVO> cjVoList = new ArrayList<CaiJiVO>();
    	for(CaiJiVO caiJiVO : caiJiVOs){
//    		System.out.println("time2:" + dateFormat.format(caiJiVO.getTime()));
    		if(caiJiVO.getDevice() == null) {
    			continue;
    		}
    		cjVoList.add(caiJiVO);
    	}
    	if(cjVoList.size() > 0) {
    		this.testRecordService.addTest(cjVoList);
    	}
    	log.error("保存采集数据耗时：" + (new Date().getTime() - sd.getTime()) + "毫秒");
    }
    /**
     * 异步处理请求
     * @param caiJiVOs
     * @param dataType
     */
    private boolean setDevice(CaiJiVO caiJiVO) {
    	Device device = deviceService.findDeviceByImeiAndAdd(caiJiVO);//获得对应设备
        if(device == null) {
//        	log.error("未找到设备：" + JSON.toJSONString(caiJiVO));
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

    /**
     * 添加设备报警
     * @param device
     * @param date
     */
    private 
//    synchronized 
    CaiJiVO saveAlarmRecord(Device device, final String preStatusText,
    		String statusText, Date date, String flag, boolean saveRecord) {
    	try {
    		// 同步方法获取设备的最新状态
    		Device dbDevice = this.deviceService.getById(device.getId());
    		if(dbDevice == null) {
    			log.error("找不到设备" + JSON.toJSON(device));
    			return null;
    		}
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
            
            String lastDataTime = (String)this.getStationLastDataTime(caiJiVO.getImei());
    		String dataTime = lastDataTime + "#" + dbDevice.getBackup3() + flag;
    		AlarmRecord alarmRecord = convertAlarmRecord(caiJiVO, device.getDeviceType(), dataTime);
			alarmRecord.setBackup2(device.getId());
			alarmRecord.setBackup3(caiJiVO.getStatusBit());
			if(saveRecord) { // 数据传入时更新设备状态&时间
				// 查询是否有设备处于通讯故障
	            Map<String, Object> idMap = this.alarmRecordService.selectConnectionError(caiJiVO);
	            if(idMap != null && idMap.size() > 0) {
	            	// 全部标记为通讯恢复
	            	this.alarmRecordService.updateConnectionRecovery(caiJiVO);
	            	alarmRecord.setAlarmState(1);
	            	alarmRecord.setTstatus(this.getRecordStatus(dbDevice, caiJiVO.getPv(), "通讯恢复"));
	            	alarmRecordService.insertAlarm(alarmRecord);
	            }
	            // 同步数据库状态时，保留值
	            caiJiVO.setRtStatus(placeText);
//				deviceService.syncRealDataById(caiJiVO);
			}
			// 对比上一次数据状态，有变化先恢复
			if(StringUtils.isNotEmpty(preStatusText)) {
				String alarmRecordKey = "alarmRecord#" + device.getId();
	            Object alarmVal = redis.get(alarmRecordKey);
	            // 避免同一设备同一类型重复解除
	            if(alarmVal == null || !alarmVal.toString().equals(preStatusText + "#" + dataTime)) {
	            	alarmRecord.setAlarmState(1);
	            	alarmRecord.setTstatus(this.getRecordStatus(dbDevice, caiJiVO.getPv(), preStatusText));
	            	
	            	alarmRecordService.insertAlarm(alarmRecord);
	            	redis.set(alarmRecordKey, preStatusText + "#" + dataTime);
	            	
	            	// 恢复报警移除redis
	    			String alarmKey = "alarm#" + device.getId();
	    			redis.del(alarmKey);
	            }
			}
			if("正常".equals(device.getStatusType())) { // 设备恢复正常，重置报警位
				String alarmKey = "alarm#" + device.getId();
    			redis.del(alarmKey);
				String alarmRecordKey = "alarmRecord#" + device.getId();
    			redis.del(alarmRecordKey);
			}
    		// 监控状态=设备状态 不重复报警
    		if(statusText.equals(device.getStatusType())){
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
//            if(!saveRecord) {
//            	//非数据添加时，添加监测记录表：报警
//            	List<CaiJiVO> caijiList = new ArrayList<CaiJiVO>();
//            	caijiList.add(caiJiVO);
//            	testRecordService.addTest(caijiList);
//            }
            if(!"正常".equals(statusText)) {
    			String alarmKey = "alarm#" + device.getId();
                Object alarmVal = redis.get(alarmKey);
                // 避免同一设备类型重复报警
                if(alarmVal == null || !alarmVal.toString().equals(statusText)) {
                	alarmRecord.setAlarmState(0);
                	alarmRecord.setTstatus(SattusSplitUtil.getStatus1(caiJiVO));
                    //添加报警表
                    alarmRecordService.insertAlarm(alarmRecord);
                    //添加报警短信表
                    alarmSmsService.insertSms(alarmRecord);
                    redis.set(alarmKey, statusText);
                }
            }
            //更新设备表
            if(!saveRecord) {
            	caiJiVO.setTime(null);
//            	deviceService.syncRealDataById(caiJiVO);
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
    
    public static void main(String[] args) {
//    	String dataStr = "NO=42;IMEI=866262045791299;ICCID=89860411101893280735;SS=31;"
//    			+ "ADD=1;STATUS=3;"
//    			+ "ADD=1001;STATUS=2;PV=28;"
//    			+ "ADD=1002;STATUS=0;PV=0;"
//    			+ "ADD=1003;STATUS=0;PV=0;"
//    			+ "ADD=1004;STATUS=1;PV=0;"
//    			+ "ADD=2;STATUS=1;"
//    			+ "ADD=2001;STATUS=1;PV=0;"
//    			+ "ADD=2002;STATUS=1;PV=0;"
//    			+ "ADD=2003;STATUS=1;PV=0;"
//    			+ "ADD=2004;STATUS=1;PV=0;";
    	
    	String dataStr = "NO=42;IMEI=866262045791299;";
    	
    	ArrayList<CaiJiVO> caiJiVOs = new CaiJiController().convertData(dataStr);
        System.out.println(JSONArray.toJSON(caiJiVOs));
        
    }
}
