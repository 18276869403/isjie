package org.jeecg.modules.demo.monitor.service;

import org.jeecg.modules.demo.monitor.entity.FloorPlan;
import org.jeecg.modules.demo.monitor.vo.DeviceFloor;
import org.jeecg.modules.demo.monitor.vo.DevicePlace;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IDevicePlaceService extends IService<FloorPlan> {
    DevicePlace findDevicePlaceByFloorPlanId(Integer id, Integer Fid, String userId);

    DeviceFloor findDeviceAndFloorPlanByFId(Integer fid, String userId);

    List<FloorPlan> findFloorByDeviceId(Integer did, Integer cid);
}
