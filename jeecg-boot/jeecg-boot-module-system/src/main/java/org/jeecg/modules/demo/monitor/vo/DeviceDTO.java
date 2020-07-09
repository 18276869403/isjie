package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;

import java.util.List;


@Data
public class DeviceDTO {
    private Integer id;//id
    private String deviceType;//设备类型
    private String deviceImei;//设备编号
    private String deviceNum; //设备编号
    private String devicePositionNum;//设备位号
    private XyAxisDTO xyAxisDTO;//设备xy
    private String statusType; //设备状态
    private String xposition;//设备保存的x坐标
    private String yposition;//设备保存的y坐标
    private String picUrl;//图标

}
