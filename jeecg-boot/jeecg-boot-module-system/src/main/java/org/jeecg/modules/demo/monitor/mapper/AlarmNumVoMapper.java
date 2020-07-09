package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.vo.AlarmNumVo;
import org.jeecg.modules.demo.monitor.vo.AlarmNumVo2;
import org.jeecg.modules.demo.monitor.vo.AlarmServenVO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 报警记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface AlarmNumVoMapper extends BaseMapper<AlarmNumVo> {
	public List<AlarmNumVo> selectSevenDayAll1(@Param("customerId") String customerId);
	public List<AlarmNumVo> selectSevenDayAll2(@Param("customerId") String customerId);
	public List<AlarmNumVo> selectSevenDayAll3(@Param("customerId") String customerId);

	public List<AlarmNumVo2> selectOneDayTop10(@Param("customerId") String customerId);

	public List<AlarmNumVo> selectSevenDayOne(@Param("deviceId") Integer deviceId);
	public List<AlarmNumVo> selectOneDay(@Param("deviceId") Integer deviceId);

	List<AlarmNumVo> selectSevenDayAll(AlarmServenVO alarmServenVO);

	List<AlarmNumVo2> selectOneTop10(AlarmServenVO alarmServenVO);
}
