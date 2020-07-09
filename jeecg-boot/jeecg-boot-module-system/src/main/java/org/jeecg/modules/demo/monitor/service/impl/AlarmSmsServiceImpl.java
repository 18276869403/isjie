package org.jeecg.modules.demo.monitor.service.impl;

import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.entity.AlarmSms;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.mapper.AlarmSmsMapper;
import org.jeecg.modules.demo.monitor.service.IAlarmSmsService;
import org.jeecg.modules.demo.monitor.vo.AlarmSmsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;


/**
 * @Description: 报警短信表
 * @Author: jeecg-boot
 * @Date:   2020-01-13
 * @Version: V1.0
 */
@Service
public class AlarmSmsServiceImpl extends ServiceImpl<AlarmSmsMapper, AlarmSms> implements IAlarmSmsService {
@Autowired AlarmSmsMapper alarmSmsMapper;

@Override
public IPage<AlarmSmsVo> selectAlarmSms(Page<AlarmSmsVo> page, Wrapper<AlarmSmsVo> wrapper) {
	
	return alarmSmsMapper.selectAlarmSms(page, wrapper);
}

	/*
	 * @Override public IPage<AlarmSmsVo> selectAlarmSms(Page<AlarmSmsVo> page,
	 * Wrapper<AlarmSmsVo> wrapper) { return alarmSmsMapper.selectAlarmSms(page,
	 * wrapper); }
	 */

	@Override
	public List<AlarmSms> findAllAlarm() {

		return alarmSmsMapper.findAllAlarm();
	}

	@Override
	public Device findDevice(AlarmSms alarmSms) {
		return alarmSmsMapper.findDevice(alarmSms.getImei(),alarmSms.getEquipAdd(),alarmSms.getEquipType());
	}

	@Override
	public void updateStatus(Integer id, Date date) {
		alarmSmsMapper.updateStatus(id,date);
	}

	@Override
	public int insertSms(AlarmRecord alarmRecord) {
		return alarmSmsMapper.insertSms(alarmRecord);
	}

	@Override
	public void deleteAll() {
		alarmSmsMapper.deleteAll();
	}


}
