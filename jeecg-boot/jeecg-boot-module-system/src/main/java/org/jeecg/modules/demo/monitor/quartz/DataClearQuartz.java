package org.jeecg.modules.demo.monitor.quartz;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.entity.Configuration;
import org.jeecg.modules.demo.monitor.entity.TestRecord;
import org.jeecg.modules.demo.monitor.service.IAlarmRecordService;
import org.jeecg.modules.demo.monitor.service.IConfigurationService;
import org.jeecg.modules.demo.monitor.service.ITestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Component
public class DataClearQuartz {
	@Autowired
	private IConfigurationService configurationService;
	@Autowired
	private ITestRecordService recordService;
	@Autowired
	private IAlarmRecordService alarmRecordService;
	// 每天0点执行
	@Scheduled(cron = "0 0 0 * * ?")
//	@Scheduled(cron = "1/5 * * * * ?")
    public void testScheduled(){
		QueryWrapper<Configuration> query = new QueryWrapper<Configuration>();
		Configuration con = configurationService.getOne(query);
		
		if(con == null) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		
		if(con.getMonitorDataMonth() != null && con.getMonitorDataMonth() > 0) {
			QueryWrapper<TestRecord> delQuery = new QueryWrapper<TestRecord>();
			now.add(Calendar.MONTH, -con.getMonitorDataMonth());
			delQuery.apply(" Acquisition_time < {0}", sdf.format(now.getTime()));
			recordService.remove(delQuery);
		}
		if(con.getAlarmDataMonth() != null && con.getAlarmDataMonth() > 0) {
			QueryWrapper<AlarmRecord> delQuery = new QueryWrapper<AlarmRecord>();
			now.add(Calendar.MONTH, -con.getAlarmDataMonth());
			delQuery.apply(" alarm_time < {0}", sdf.format(now.getTime()));
			alarmRecordService.remove(delQuery);
		}
		
		
    }
}
