package org.jeecg.modules.demo.monitor.quartz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.monitor.DeviceTypeConf;
import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.service.IAlarmRecordService;
import org.jeecg.modules.demo.monitor.service.IAlarmSmsService;
import org.jeecg.modules.demo.monitor.service.IDeviceService;
import org.jeecg.modules.demo.monitor.util.SattusSplitUtil;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FourGCheckQuartz {
    @Autowired
    private IAlarmRecordService alarmRecordService;
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IAlarmSmsService alarmSmsService;
    @Autowired
    private RedisUtil redis;
	// dcs 设备状态暂存
    //private volatile Map<String, Map<Integer, String>> dscDeviceStatusMap = new HashMap<String, Map<Integer, String>>();
    // 4G设备监控状态暂存 key=设备id，value=二进制设备状态码
    // private volatile Map<Integer, String> tcpDeviceStatusMap = new HashMap<Integer, String>();
    // 4G immei心跳时间暂存
    //private volatile Map<String, Date> immeiDeviceHeatMap = new HashMap<String, Date>();
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 实时设备数据及状态map
//    private volatile Map<String, Device> realDeviceList = new HashMap<String, Device>();
    // 定时检测通讯故障的周期时间（秒）
    @Value("${collection.monitor}")
    private int COM_MONITOR;
    
//    1：一个设备（IMEI+ADD），判断12分钟没新数据为通讯故障
//    2：一个IMEI号，5分钟没收到心跳包或数据包，整个IMEI下的所有设备全变通讯故障
//    2：一个IMEI号，5分钟没收到心跳包，整个IMEI下的所有设备全变通讯故障
    // 设备通讯故障时间策略（毫秒）
    private int COM_ERROR_MIN ;
    @Value("${collection.com}")
    // 兼容表达式配置
    public void setCom(String com) {
        ScriptEngineManager manager = new ScriptEngineManager();  
        ScriptEngine engine = manager.getEngineByName("js");  
    	try {
			this.COM_ERROR_MIN = Integer.parseInt(engine.eval(com).toString());
		} catch ( Exception e) {
			e.printStackTrace();
		}
//    	this.startTimer();
    }
    
    private boolean isFirst = true;
    
    public synchronized void check() {
    	log.debug("准备定时检测任务" + COM_MONITOR + "秒  设备类型：" + DeviceTypeConf.getType());
    	//system.out.println("准备定时检测任务" + COM_MONITOR + "秒  设备类型：" + DeviceTypeConf.getType());
    	// 首次读取状态
    	if(isFirst) {
    		isFirst = false;
	    	try {
		    	List<Device> dsList = this.deviceService.groupByImei();
		    	if(dsList != null && dsList.size() > 0) {
			        for(Device device : dsList){
			    		if(StringUtils.isNotEmpty(device.getBackup3())) {
			    			Date lastHeatUpdateTime = dateFormat.parse(device.getBackup3());
			    			this.redis.set("imei#" + device.getDeviceImei(), lastHeatUpdateTime);
			    		}
			    		if(StringUtils.isNotEmpty(device.getBackup4())) {
			    			this.redis.set("tcp#device#" + device.getId(), device.getBackup4());
			    		}
		    		}
		    	}
	    	}catch(Exception e) {
	    		log.error("首次读取设备心跳数据异常", e);
	    	}
    	}
		if(COM_MONITOR > 0 && DeviceTypeConf.is4G()) {
	    	// 启动主动检测通讯故障的定时任务
        	Date date = new Date();
            log.debug(dateFormat.format(date) + " 开始进行通讯&心跳故障检测");
            //system.out.println(dateFormat.format(date) + " 开始进行通讯&心跳故障检测");
        	if(COM_ERROR_MIN < 0 && HEAT_ERROR_MIN < 0) {
            	return;//未设置时不执行
            }
            //无数据的设备状态更新
            QueryWrapper<Device> queryWrapper = new QueryWrapper<Device>();
            queryWrapper.ne("status_type", "通讯故障");
            List<Device> devices = deviceService.list(queryWrapper);
	        
	        log.debug("开始验证设备数" + devices.size());
            //system.out.println("开始验证设备数" + devices.size());
            
            if(devices == null || devices.size() == 0) {
            	return;
            }
            try {
            	List<CaiJiVO> cvs = new ArrayList<CaiJiVO>();
    	        for(Device device : devices){
    	        	date = new Date();
    	        	CaiJiVO cv = null;
    	            if (COM_ERROR_MIN > 0) { // 数据时间：通讯故障检测
    	            	String lastDataTime = device.getBackup1();
//	        	            	String lastDataTime = (String)getStationLastDataTime(device.getDeviceImei()) ;// device.getBackup1();
    	            	if(StringUtils.isNotBlank(lastDataTime)) {
        	            	Date parse = dateFormat.parse(device.getBackup1());
//	            	            	Date parse = dateFormat.parse(lastDataTime); //dateFormat.parse(device.getBackup1());
        	            	if(date.getTime() - parse.getTime() > COM_ERROR_MIN) {//数据通讯故障超时判断
        	            		cv = saveAlarmRecord(device, null, "通讯故障", date, "#D", false);
        	            	}
    	            	}
    	            } 
    	            if(cv == null && HEAT_ERROR_MIN > 0 && 
    	            		StringUtils.isNotBlank(device.getBackup3())){ // 心跳故障超时时间判断
		    			Date lastHeatUpdateTime =  dateFormat.parse(device.getBackup3());//;(Date)redis.get("imei#" + device.getDeviceImei());
	            		boolean isLimit = lastHeatUpdateTime != null &&
	            				date.getTime() - lastHeatUpdateTime.getTime() > HEAT_ERROR_MIN;
    	        		// 心跳超时
    	    	    	if(isLimit) {
    	    	    		cv = saveAlarmRecord(device, null, "通讯故障", date, "#H", false);
    	    	    	}
    	            }
    	            if(cv != null) {
            			cvs.add(cv);
            		}
    	        }
    	        
    	        log.debug("同步通讯故障设备数" + cvs.size());
                //system.out.println("同步通讯故障设备数" + cvs.size());
                
    	        if(cvs.size() > 0) {
    	        	deviceService.syncRealDataById(cvs);
    	        }
    	        // 
            }catch (Exception e) {
            	log.error("添加无数据报警异常", e);
    		}
    	}
    }
    // 4G设备：心跳故障时间策略（毫秒）
    private int HEAT_ERROR_MIN;
    @Value("${collection.heat}")
    // 兼容表达式配置
    public void setHeat(String heat) {
        ScriptEngineManager manager = new ScriptEngineManager();  
        ScriptEngine engine = manager.getEngineByName("js");  
    	try {
			this.HEAT_ERROR_MIN = Integer.parseInt(engine.eval(heat).toString());
		} catch ( Exception e) {
			e.printStackTrace();
		}
    }
	
	@Scheduled(cron = "1/10 * * * * ?")
    public void testScheduled(){
		this.check();
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
            
            String lastDataTime = device.getBackup1();
//            Date d = (Date)redis.get("imei#" + device.getDeviceImei());
//            if(d != null) {
//            	lastDataTime = dateFormat.format(d);
//            }
            
//            String lastDataTime = (String)this.getStationLastDataTime(caiJiVO.getImei());
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

//    private Object getStationLastDataTime(String key) {
//    	return this.redis.get("device#lastDataTime#station#" + key);
//    }
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
}
