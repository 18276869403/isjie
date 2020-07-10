package org.jeecg.modules.demo.monitor.service;

import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.entity.AlarmSms;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.vo.AlarmRecordVo;
import org.jeecg.modules.demo.monitor.vo.AlarmSmsVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;


/**
 * @Description: 报警短信表
 * @Author: jeecg-boot
 * @Date:   2020-01-13
 * @Version: V1.0
 */
public interface IAlarmSmsService extends IService<AlarmSms> {
	//查询报警短信所有信息
	/*
	 * public IPage<AlarmSmsVo> selectAlarmSms(Page<AlarmSmsVo> page,
	 * Wrapper<AlarmSmsVo>wrapper);
	 */

	public IPage<AlarmSmsVo> selectAlarmSms(Page<AlarmSmsVo> page, Wrapper<AlarmSmsVo> wrapper);

	List<AlarmSms> findAllAlarm();

	Device findDevice(AlarmSms alarmSms);

	void updateStatus(Integer id, Date date);

    int insertSms(AlarmRecord alarmRecord);

    void deleteAll();
}
