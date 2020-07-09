package org.jeecg.modules.demo.monitor.service.impl;

import java.util.List;

import org.jeecg.modules.demo.monitor.mapper.AlarmNumVoMapper;
import org.jeecg.modules.demo.monitor.service.IAlarmNumVoService;
import org.jeecg.modules.demo.monitor.vo.AlarmNumVo;
import org.jeecg.modules.demo.monitor.vo.AlarmNumVo2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 监测记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Service
public class AlarmNumVoServiceImpl extends ServiceImpl<AlarmNumVoMapper,AlarmNumVo > implements IAlarmNumVoService {
	@Autowired
	private AlarmNumVoMapper alarmNumVoMapper;

	@Override
	public List<AlarmNumVo> selectSevenDayAll1(String customerId) {
		return alarmNumVoMapper.selectSevenDayAll1(customerId);
	}

	@Override
	public List<AlarmNumVo> selectSevenDayAll2(String customerId) {
		return alarmNumVoMapper.selectSevenDayAll2(customerId);
	}

	@Override
	public List<AlarmNumVo> selectSevenDayAll3(String customerId) {
		return alarmNumVoMapper.selectSevenDayAll3(customerId);
	}

//	@Override
//	public List<AlarmNumVo> selectSevenDayOne(String alarmDeviceImei, String alarmAddress) {
//		return alarmNumVoMapper.selectSevenDayOne(alarmDeviceImei,alarmAddress);
//	}
//
//	@Override
//	public List<AlarmNumVo> selectOneDay(String alarmDeviceImei, String alarmAddress) {
//		return alarmNumVoMapper.selectOneDay(alarmDeviceImei, alarmAddress);
//	}

	@Override
	public List<AlarmNumVo2> selectOneDayTop10(String customerId) {
		return alarmNumVoMapper.selectOneDayTop10(customerId);
	}

}
