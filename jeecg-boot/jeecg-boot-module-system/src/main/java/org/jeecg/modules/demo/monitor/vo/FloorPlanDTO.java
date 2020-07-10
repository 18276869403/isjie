package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;

@Data
public class FloorPlanDTO {
    private Integer id;         //楼层id
    private String floorName;   //楼层名
    private String planPic;     //楼层图
    private String showIndex;   //序号
}
