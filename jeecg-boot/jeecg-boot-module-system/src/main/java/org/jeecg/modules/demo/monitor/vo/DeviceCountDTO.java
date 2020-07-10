package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;

@Data
public class DeviceCountDTO {

    private Integer deviceCount;//设备总数量
    private Integer deviceNormalCount;//正常设备数量
    private Integer deviceDefendCount;//维护设备数量
    private Integer deviceFaultCount;//设备故障数量
    private Integer deviceCommFailCount;//通讯故障数量
    private Integer deviceAlertCount;//报警设备数量

}
