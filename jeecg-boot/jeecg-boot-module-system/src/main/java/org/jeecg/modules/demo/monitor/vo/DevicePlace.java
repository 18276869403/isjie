package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;
import org.jeecg.modules.demo.monitor.vo.DeviceDTO;
import org.jeecg.modules.demo.monitor.vo.FloorPlanDTO;

import java.util.List;

@Data
public class DevicePlace {
    private List<DeviceDTO> deviceDTOS;//设备布点
    private List<FloorPlanDTO>  floorPlanDTOS;//楼层信息
}
