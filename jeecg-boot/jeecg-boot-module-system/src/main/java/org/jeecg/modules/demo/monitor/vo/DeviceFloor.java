package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeviceFloor {
    private List<DeviceDTO> deviceDTOS;//设备布点

    private FloorPlanDTO  floorPlanDTO;//楼层信息
}
