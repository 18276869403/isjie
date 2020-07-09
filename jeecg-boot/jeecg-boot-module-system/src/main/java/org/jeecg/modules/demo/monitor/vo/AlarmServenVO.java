package org.jeecg.modules.demo.monitor.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @program: jeecg-boot-parent
 * @description: 七天报警请求实体
 * @author: llm
 * @create: 2020-02-11 17:47
 **/
@Data
public class AlarmServenVO {

    /**用户id**/
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**省份ID*/
    @ApiModelProperty(value = "省份ID")
    private Integer oneAreaId;

    /**城市ID*/
    @ApiModelProperty(value = "城市ID")
    private Integer twoAreaId;

    /**客户ID*/
    @ApiModelProperty(value = "客户ID")
    private Integer customerId;

    /**设备类型名称*/
    @ApiModelProperty(value = "设备类型名称")
    private String alarmDeviceType;

    /**楼层名*/
    @ApiModelProperty(value = "楼层名")
    private String floorName;

    /**设备编号*/
    @ApiModelProperty(value = "设备编号")
    private String deviceNum;

    /**设备编号*/
    @ApiModelProperty(value = "设备id")
    private String deviceId;
}
