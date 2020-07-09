package org.jeecg.modules.demo.monitor.quartz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ComCheckQuartz {
    @Autowired
    private IAlarmRecordService alarmRecordService;
    @Autowired
    private IDeviceService deviceService;
//    @Autowired
//    private ITestRecordService testRecordService;
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
    }
    @Scheduled(cron = "1/10 * * * * ?")
    public void testScheduled(){
		this.init();
	}
	/**
	 * 
	 */
	public synchronized void init() {
    	if(COM_MONITOR > 0  && COM_ERROR_MIN > 0 && DeviceTypeConf.isCom()) {
        	Date date = new Date();
        	log.info(dateFormat.format(date) + " 开始COM设备通讯故障检测");
            QueryWrapper<Device> queryWrapper = new QueryWrapper<Device>();
            queryWrapper.ne("status_type", "通讯故障");
            List<Device> devices = deviceService.list(queryWrapper);
            if(devices == null || devices.size() == 0) {
            	return;
            }
            try {
            	List<CaiJiVO> cvs = new ArrayList<CaiJiVO>();
    	        for(Device device : devices){
    	        	if("通讯故障".equals(device.getStatusType())) {
    	        		continue;
    	        	}
	            	if(StringUtils.isNotBlank(device.getAddressNumber()) && device.getAddressNumber().length() > 3) {
    	            	String lastDataTime = (String)getStationLastDataTime(device.getAddressNumber().substring(0, 3)) ;// device.getBackup1();
    	            	if(StringUtils.isNotBlank(lastDataTime)) {
        	            	Date parse = dateFormat.parse(lastDataTime); //dateFormat.parse(device.getBackup1());
        	            	if(date.getTime() - parse.getTime() > COM_ERROR_MIN) {//数据通讯故障超时判断
        	            		device.setBackup1(lastDataTime);
        	            		CaiJiVO cv = saveAlarmRecord(device.getId(), "通讯故障", date, "#D");
        	            		if(cv != null) {
        	            			cvs.add(cv);
        	            		}
        	            	}
    	            	}
	            	}
    	        }
    	        if(cvs.size() > 0) {
    	        	deviceService.syncRealDataById(cvs);
    	        }
    	        // 
            }catch (Exception e) {
            	log.error("添加无数据报警异常", e);
    		}
    	}
    }
    private Object getStationLastDataTime(String key) {
    	return this.redis.get("device#lastDataTime#station#" + key);
    }
    

    /**
     * 添加设备报警
     * @param device
     * @param date
     */
    private 
//    synchronized 
    CaiJiVO saveAlarmRecord(Integer deviceId, String statusText, Date date, String flag) {
    	try {
			Date logDate    = new Date();
			Device device   = this.deviceService.getById(deviceId);
    		this.logger("读取设备实时状态耗时：", logDate);// + (new Date().getTime() - logDate.getTime()) + "毫秒");

    		// 监控状态=设备状态 不重复报警
    		if(statusText != null && statusText.equals(device.getStatusType())){
    			return null;
    		}
    		
    		CaiJiVO caiJiVO = new CaiJiVO();
    		caiJiVO.setStatusBit(device.getBackup4());
            caiJiVO.setStatus(statusText);
            caiJiVO.setRtStatus(statusText);
            caiJiVO.setTime(date);
            caiJiVO.setImei(device.getDeviceImei());
            caiJiVO.setAdd(device.getAddressNumber());
            caiJiVO.setIccid(device.getIccid());
            caiJiVO.setDeviceId(device.getId());
            caiJiVO.setPv(device.getBackup2());
            
            String lastDataTime = (String)this.getStationLastDataTime(caiJiVO.getAdd().substring(0,  3));
    		String dataTime = lastDataTime + "#" + device.getBackup3() + flag;
    		AlarmRecord alarmRecord = convertAlarmRecord(caiJiVO, device.getDeviceType(), dataTime);
			alarmRecord.setBackup2(device.getId());
			alarmRecord.setBackup3(caiJiVO.getStatusBit());
            
            caiJiVO.setCustomerId(device.getCustomerId());
            caiJiVO.setDataTime(dataTime);
            caiJiVO.setStatusBit(device.getBackup4());
            
            if(device.getBackup2() != null){
                caiJiVO.setPv("0");
            }
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
            //更新设备表
        	caiJiVO.setTime(null);
            return caiJiVO;
        } catch (Exception e) {
        	log.error("添加报警异常", e);
        }
    	return null;
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

    private void logger(String text, Date preDate) {
//    	Date now = new Date();
//    	long ex = now.getTime() - preDate.getTime();
//    	log.error(text + ex + "毫秒 " 
//    			+ (ex > 10000 ? "大于10秒" : "")
//    			+ (ex > 60 * 1000 ? "大于1分钟" : "")
//    			+ (ex > 2 * 60 * 1000 ? "大于2分钟" : "")
//    			+ (ex > 3 * 60 * 1000 ? "大于3分钟" : "")
//    			+ (ex > 4 * 60 * 1000 ? "大于4分钟" : "")
//    			+ (ex > 5 * 60 * 1000 ? "大于5分钟" : "")
//    			+ (ex > 6 * 60 * 1000 ? "大于6分钟" : "")
//    			+ (ex > 7 * 60 * 1000 ? "大于7分钟" : "")
//    			+ (ex > 8 * 60 * 1000 ? "大于8分钟" : "")
//    			+ (ex > 9 * 60 * 1000 ? "大于9分钟" : "")
//    			+ (ex > 10 * 60 * 1000 ? "大于10分钟" : "")
//    			);
    }
}
