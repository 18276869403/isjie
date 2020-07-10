package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.vo.AlarmRecordVo;
import org.jeecg.modules.demo.monitor.vo.AlarmWindowVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface AlarmRecordVoMapper extends BaseMapper<AlarmRecordVo> {

	//前端报警记录
	 IPage<AlarmRecordVo> selectAlarmAll(Page<AlarmRecordVo> page, @Param(Constants.WRAPPER) Wrapper<AlarmRecordVo> wrapper);
	 //前端报警记录
	 IPage<AlarmRecordVo> selectDeviceRealAlarm(Page<AlarmRecordVo> page, @Param(Constants.WRAPPER) Wrapper<AlarmRecordVo> wrapper);
	//前端报警记录
	IPage<AlarmRecordVo> selectAlarmVoAll(Page<AlarmRecordVo> page, @Param(Constants.WRAPPER) Wrapper<AlarmRecordVo> wrapper);

	 List<AlarmRecordVo> exportXlsAlarmAll(@Param(Constants.WRAPPER) Wrapper<AlarmRecordVo> wrapper);
//	// 根据id查询实时报警情况窗口
	 List<AlarmWindowVo> queryAlarmRecordById(@Param("id") Integer id);
	// 根据id查询用户登录所有客户实时报警情况窗口
	List<AlarmWindowVo> queryAlarmRecordByUid(@Param("uid") String uid);
}
