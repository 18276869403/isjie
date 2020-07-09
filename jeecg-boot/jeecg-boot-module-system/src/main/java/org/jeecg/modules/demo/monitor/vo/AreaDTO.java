package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;

import java.util.List;

@Data
public class AreaDTO {
    private String useId;  //用户id
    private List<Long> keys; //客户的id集合
}
