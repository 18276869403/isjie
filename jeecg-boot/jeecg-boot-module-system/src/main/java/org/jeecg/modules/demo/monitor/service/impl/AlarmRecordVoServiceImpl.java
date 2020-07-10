package org.jeecg.modules.demo.monitor.service.impl;

import java.util.List;

import org.jeecg.modules.demo.monitor.mapper.AlarmRecordVoMapper;
import org.jeecg.modules.demo.monitor.service.IAlarmRecordVoService;
import org.jeecg.modules.demo.monitor.vo.AlarmRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class AlarmRecordVoServiceImpl extends ServiceImpl<AlarmRecordVoMapper, AlarmRecordVo> implements IAlarmRecordVoService{
	
	@Autowired
	private AlarmRecordVoMapper alarmRecordVoMapper;
	
	//前端查询报警信息service实现层
	@Override
	public IPage<AlarmRecordVo> selectAlarmAll(Page<AlarmRecordVo> page,Wrapper<AlarmRecordVo>wrapper){
		return alarmRecordVoMapper.selectAlarmAll(page, wrapper);
		
	}
	//后台查询报警信息service实现层
	@Override
	public IPage<AlarmRecordVo> selectAlarmVoAll(Page<AlarmRecordVo> page, Wrapper<AlarmRecordVo> wrapper) {
		return alarmRecordVoMapper.selectAlarmVoAll(page,wrapper);
	}

	//导出查询报警信息service实现层
	@Override
	public List<AlarmRecordVo> exportXlsAlarmAll(Wrapper<AlarmRecordVo> wrapper) {
		return alarmRecordVoMapper.exportXlsAlarmAll(wrapper);
	}
	@Override
	public IPage<AlarmRecordVo> selectDeviceRealAlarm(Page<AlarmRecordVo> page, Wrapper<AlarmRecordVo> wrapper) {
		return this.alarmRecordVoMapper.selectDeviceRealAlarm(page, wrapper);
	}
}
