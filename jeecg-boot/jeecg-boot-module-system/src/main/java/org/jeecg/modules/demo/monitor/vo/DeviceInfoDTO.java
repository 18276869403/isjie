package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeviceInfoDTO {

    private Integer id;             //设备id

    private String deviceImei;      //设备imei
    private String iccid;           //设备的iccid
    private String addressNumber;   //设备的地址
    private String deviceNum;   //设备的地址


    private String ranges;      //设备量程
    private String deviceType;      //设备类型
    private String unit;            //设备单位

    private String statusType;      //设备状态
    private String xposition;       //设备保存的x坐标
    private String yposition;       //设备保存的y坐标
    private String picUrl;          //图标
    private String testPv;          //检测值
    private String deviceModel;     //设备型号
    private String detectionTarget; //检查目标


}
