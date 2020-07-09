package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;

@Data
public class DeviceTypeCount {

    private Integer count;          //总设备数量
    private Integer detectorCount;  //探测器数量
    private Integer controllerCount;//控制器数量
    private Integer outCount;       //输出模块数量
}
