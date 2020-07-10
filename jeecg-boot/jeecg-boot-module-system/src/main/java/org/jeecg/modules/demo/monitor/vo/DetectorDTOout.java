package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DetectorDTOout {

    private String testPv;//检测值
    private String acquisitionTime;//采集时间
    private String picUrl;//图标
    private String statusType; //设备状态
    private String unit; //单位


}
