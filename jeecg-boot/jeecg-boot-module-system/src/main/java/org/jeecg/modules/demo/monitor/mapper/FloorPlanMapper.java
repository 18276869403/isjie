package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.FloorPlan;
import org.jeecg.modules.demo.monitor.vo.FloorPlanDTO;
import org.jeecg.modules.demo.monitor.vo.FlorVo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 楼层图表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface FloorPlanMapper extends BaseMapper<FloorPlan> {
	IPage<FlorVo> custoMpage(Page<FlorVo> page, @Param(Constants.WRAPPER) QueryWrapper<FlorVo> queryWrapper);
	/**
	 * @param id 根据客户id查询楼层信息
	 * @param userId 用户id，用户设备权限验证
	 * @return
	 */
	List<FloorPlanDTO> findFloorPlanById(@Param("id") Integer id);
	/**
	 * @param id 根据客户id查询楼层信息
	 * @param userId 用户id，用户设备权限验证
	 * @return
	 */
	List<FloorPlanDTO> queryFloorPlan(@Param("id") Integer id,
			@Param("userId") String userId);

	/**
	 * @param fid 根据楼层id查询设备ids
	 * @return
	 */
	List<Integer> findDeviceId(@Param("fid") Integer fid, 
			@Param("userId") String userId);

	/**
	 * @param fid 根据楼层id查询楼层信息
	 * @return
	 */
	FloorPlanDTO findFloorPlanByFId(@Param("fid") Integer fid);
	
	/**
	 * @param customerId 前端根据客户id查询楼层图
	 * @return
	 */
	IPage<FlorVo> webselectFlor(Page<FlorVo> page, @Param(Constants.WRAPPER) QueryWrapper<FlorVo> queryWrapper);

	List<FloorPlan> findFloorByDeviceId(@Param("deviceId") Integer DeviceId, @Param("customerId") Integer CustomerId);

    void deletefloordevice(@Param("id") String id);
}
