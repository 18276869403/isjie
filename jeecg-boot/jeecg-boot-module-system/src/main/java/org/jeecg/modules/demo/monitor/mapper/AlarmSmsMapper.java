package org.jeecg.modules.demo.monitor.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.entity.AlarmSms;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.vo.AlarmSmsVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 报警短信表
 * @Author: jeecg-boot
 * @Date:   2020-01-13
 * @Version: V1.0
 */
public interface AlarmSmsMapper extends BaseMapper<AlarmSms> {
	//查询报警短信所有信息
	

	IPage<AlarmSmsVo> selectAlarmSms(Page<AlarmSmsVo> page, @Param(Constants.WRAPPER) Wrapper<AlarmSmsVo> wrapper);


	//查出所有未发送短信的
	List<AlarmSms> findAllAlarm();

	Device findDevice(@Param("imei") String imei, @Param("addnum") String addnum,  @Param("deviceType") String devicetype);

	void updateStatus(@Param("aid") Integer id, @Param("nowDate") Date date);

    int insertSms(AlarmRecord alarmRecord);

    void deleteAll();
}
