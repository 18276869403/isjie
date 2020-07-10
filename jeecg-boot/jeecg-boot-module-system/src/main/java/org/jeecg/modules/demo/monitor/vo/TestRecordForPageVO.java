package org.jeecg.modules.demo.monitor.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @program: jeecg-boot-parent
 * @description: 实时记录分页请求包装类
 * @author: llm
 * @create: 2020-02-18 16:12
 **/
@Data
public class TestRecordForPageVO {

    //自定义监测实体
    /**自定义主键id*/
    @ApiModelProperty(value = "id")
    private Integer id;
    /**用户ID**/
    @Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private String  userId;
    /**客户ID*/
    @Excel(name = "客户ID", width = 15)
    @ApiModelProperty(value = "客户ID")
    private Integer customerId;
    /**楼层名*/
    @Excel(name = "楼层名", width = 15)
    @ApiModelProperty(value = "楼层名")
    private String floorName;
    /**设备IMEI*/
    @Excel(name = "设备IMEI", width = 15)
    @ApiModelProperty(value = "设备IMEI")
    private String testDeviceImei;
    /**ICCID*/
    @Excel(name = "ICCID", width = 15)
    @ApiModelProperty(value = "ICCID")
    private String iccid;
    /**设备类型*/
    @Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    private String deviceType;
    /**设备编号*/
    @Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    private String deviceNum;
    /**设备位号*/
    @ApiModelProperty(value = "设备位号")
    private String devicePositionnum;
    /**探测目标*/
    @Excel(name = "探测目标", width = 15)
    @ApiModelProperty(value = "探测目标")
    private String detectionTarget;
    /**量程*/
    @Excel(name = "量程", width = 15)
    @ApiModelProperty(value = "量程")
    private java.math.BigDecimal ranges;
    /**单位*/
    @Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    private String unit;
    /**检测值*/
    @Excel(name = "检测值", width = 15)
    @ApiModelProperty(value = "检测值")
    private String testPv;
    /**状态*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private String tstatus;
    /**采集时间*/
    @Excel(name = "采集时间", width = 20, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "采集时间")
    private java.util.Date acquisitionTime;
    /**客户名*/
    @Excel(name = "客户名", width = 15)
    @ApiModelProperty(value = "客户名")
    private String name;
    /**项目名*/
    @Excel(name = "项目名", width = 15)
    @ApiModelProperty(value = "项目名")
    private String projectName;
    /**省份*/
    @Excel(name = "省份", width = 15)
    @ApiModelProperty(value = "省份")
    private String oneAreaName;
    /**设备地址信息(对应设备表的Address_number)*/
    @Excel(name = "设备地址信息", width = 15)
    @ApiModelProperty(value = "设备地址信息")
    private String testAddress;
    /**省份ID*/
    @ApiModelProperty(value = "省份ID")
    private Integer oneAreaId;
    /**城市ID*/
    @ApiModelProperty(value = "城市ID")
    private Integer twoAreaId;
    /**城市*/
    @Excel(name = "城市", width = 15)
    @ApiModelProperty(value = "城市")
    private String twoAreaName;

    @ApiModelProperty(value = "所在页")
    private Integer pageNo;
    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;
//    @Excel(name = "开始时间", width = 20, format = "yyyy-MM-dd")
//    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
//    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "开始时间")
    private String  acquisitionTime_begin;
//    @Excel(name = "结束时间", width = 20, format = "yyyy-MM-dd")
//    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
//    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "结束时间")
    private String acquisitionTime_end;
    
    @ApiModelProperty(value = "设备id")
    private Integer deviceId;


}
