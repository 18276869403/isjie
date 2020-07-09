package org.jeecg.modules.demo.monitor.vo;


import lombok.Data;

@Data
public class DeviceXY {
    private Integer did;
    private Integer cid;
    private Integer fid;
    private String xaxis;
    private String yaxis;
}
