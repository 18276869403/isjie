package org.jeecg.modules.demo.monitor.vo;

import org.jeecg.modules.demo.monitor.entity.Customers;

import java.util.List;

/**
 * @author 鞠 玮
 * @date 2020/7/9 10:57
 */
public class AreaFloorPlanVo {
    /**
     * 区域布点
     */
    private List<DeviceAreaVO> deviceAreaVOS;

    /**
     * 客户信息，备用字段1 backup1 为鸟瞰图
     */
    private Customers customers;
}
