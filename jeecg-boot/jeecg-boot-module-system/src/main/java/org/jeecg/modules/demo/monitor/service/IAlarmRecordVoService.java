package org.jeecg.modules.demo.monitor.service;

import java.util.List;

import org.jeecg.modules.demo.monitor.vo.AlarmRecordVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IAlarmRecordVoService  extends IService<AlarmRecordVo> {
	//前端查询报警表所有信息
	public IPage<AlarmRecordVo> selectAlarmAll(Page<AlarmRecordVo> page, Wrapper<AlarmRecordVo> wrapper);
	//前端查询报警表所有信息
	public IPage<AlarmRecordVo> selectDeviceRealAlarm(Page<AlarmRecordVo> page, Wrapper<AlarmRecordVo> wrapper);
	//后台查询报警表所有信息
	public IPage<AlarmRecordVo> selectAlarmVoAll(Page<AlarmRecordVo> page, Wrapper<AlarmRecordVo> wrapper);

	//导出查询报警信息
	public  List<AlarmRecordVo> exportXlsAlarmAll(Wrapper<AlarmRecordVo> wrapper);
}
