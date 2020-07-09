package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;



@Data
public class DetectorDTO {

    private Integer id;//设备的id
    private String deviceImei;//设备的imei
    private String iccid;//设备的iccid
    private String addressNumber;//设备的地址

}
