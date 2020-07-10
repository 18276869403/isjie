package org.jeecg.modules.demo.monitor.service;


import java.util.List;

import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.vo.AlarmNumVo;
import org.jeecg.modules.demo.monitor.vo.AlarmNumVo2;
import org.jeecg.modules.demo.monitor.vo.AlarmRecordVo;
import org.jeecg.modules.demo.monitor.vo.TestRecordVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IAlarmNumVoService   extends IService<AlarmNumVo> {
	//查询当前客户七天的设备每天报警次数
	public List<AlarmNumVo> selectSevenDayAll1(String customerId);

	//查询当前客户七天的设备每天报警次数
	public List<AlarmNumVo> selectSevenDayAll2(String customerId);

	//查询当前客户七天的设备每天报警次数
	public List<AlarmNumVo> selectSevenDayAll3(String customerId);

	//查询当前客户1天内每小时设备报警次数Top10
	public List<AlarmNumVo2> selectOneDayTop10(String customerId);

//	//查询当前设备七天的每天报警次数
//	public List<AlarmNumVo> selectSevenDayOne(String alarmDeviceImei, String alarmAddress);
//
//	//查询当前设备1天内每小时报警次数
//	public List<AlarmNumVo> selectOneDay(String alarmDeviceImei, String alarmAddress);

}
