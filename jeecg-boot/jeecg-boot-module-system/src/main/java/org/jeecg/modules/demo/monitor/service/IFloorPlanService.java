package org.jeecg.modules.demo.monitor.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.FloorPlan;
import org.jeecg.modules.demo.monitor.vo.DeviceInfoDTO;
import org.jeecg.modules.demo.monitor.vo.FloorPlanDTO;
import org.jeecg.modules.demo.monitor.vo.FlorVo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 楼层图表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface IFloorPlanService extends IService<FloorPlan> {
	IPage<FlorVo> custoMpage(Page<FlorVo> page, @Param(Constants.WRAPPER) QueryWrapper<FlorVo> queryWrapper); 
	IPage<FlorVo> webselectFlor(Page<FlorVo> page, @Param(Constants.WRAPPER) QueryWrapper<FlorVo> queryWrapper);

    List<FloorPlanDTO> queryFloorNameAndIdByCid(Integer cid, String userId);

	List<DeviceInfoDTO> queryFloorDeviceByFid(Integer fid, String userId);

	void saveFloorAndDevice(FloorPlan floorPlan)throws Exception;

    void deletefloordevice(String id);
}
