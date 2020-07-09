package org.jeecg.common.system.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TestVO {
    private String imei;
    private String iccid;
    private String add;
    private String status;
    private String pv;
    private Date time;
}
