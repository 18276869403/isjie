package org.jeecg.modules.demo.monitor.entity;

import java.util.Date;

import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Data
@TableName("device")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="device对象", description="设备表")
public class Device {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**客户ID*/
	@Excel(name = "客户ID", width = 15)
    @ApiModelProperty(value = "客户ID")
	private Integer customerId;
	/**设备类型名称 1探测器，2控制器，3输出模块*/
	@Excel(name = "设备类型名称", width = 15,dicCode="Device_type")
	@Dict(dicCode = "Device_type")
    @ApiModelProperty(value = "设备类型名称")
	private String deviceType;
	/**设备IMEI*/
	@Excel(name = "设备IMEI", width = 15)
    @ApiModelProperty(value = "设备IMEI")
	private String deviceImei;
	/**设备位号*/
	@Excel(name = "设备位号", width = 15)
    @ApiModelProperty(value = "设备位号")
	private String devicePositionnum;
	/**ICCID*/
	@Excel(name = "ICCID", width = 15)
    @ApiModelProperty(value = "ICCID")
	private String iccid;
	/**地址编号*/
	@Excel(name = "设备ADD/485地址", width = 15)
    @ApiModelProperty(value = "设备ADD/485地址")
	private String addressNumber;
	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
	private String deviceNum;
	/**设备型号*/
	@Excel(name = "设备型号", width = 15)
    @ApiModelProperty(value = "设备型号")
	private String deviceModel;
	/**出厂编号*/
	@Excel(name = "出厂编号", width = 15)
    @ApiModelProperty(value = "出厂编号")
	private String factoryNum;
	/**检测目标*/
	@Excel(name = "检测目标", width = 15)
    @ApiModelProperty(value = "检测目标")
	private String detectionTarget;
	/**量程*/
	@Excel(name = "量程", width = 15)
    @ApiModelProperty(value = "量程")
	private java.math.BigDecimal ranges;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
	private String unit;
	
	/**设备报警类型*/
	@Excel(name = "设备状态", width = 15)
    @ApiModelProperty(value = "设备状态")
	private String statusType;

	/**换算系数*/
	@Excel(name = "换算系数", width = 15)
    @ApiModelProperty(value = "换算系数")
	private java.math.BigDecimal reductionCoefficient;
	
	/** 安装位置 */
	@Excel(name = "安装位置", width = 15)
	@ApiModelProperty(value = "安装位置")
	private String installationPosition;
	/**检测值超标报警值*/
	@Excel(name = "检测值超标报警值", width = 15)
    @ApiModelProperty(value = "检测值超标报警值")
	private java.math.BigDecimal exceedingAlarm;
//	/**状态报警符*/
//	@Excel(name = "状态报警符", width = 15)
//    @ApiModelProperty(value = "状态报警符")
//	private String statusAlarm;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private Object remark;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**删除状态(0-正常,1-已删除)*/
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	private Integer delFlag;
	/**备用字段1*/
    @ApiModelProperty(value = "备用字段1：设备最后数据更新时间")
	private String backup1;
	/**备用字段2*/
    @ApiModelProperty(value = "备用字段2：设备最新检测值")
	private String backup2;
	/**备用字段3*/
    @ApiModelProperty(value = "备用字段3：设备心跳最后更新时间")
	private String backup3;
	/**备用字段4*/
    @ApiModelProperty(value = "备用字段4：设备最新二进制状态数据")
	private String backup4;
	/**备用字段5*/
    @ApiModelProperty(value = "备用字段5")
	private String backup5;
    
    /**一级区域ID*/
	@ApiModelProperty(value = "一级区域ID")
	@TableField(exist = false)
	private Integer oneAreaId;
	/**二级区域Id*/
	@TableField(exist = false)
	@ApiModelProperty(value = "二级区域Id")
	private Integer twoAreaId;
	/**设备区域Id*/
	@ApiModelProperty(value = "设备区域Id")
	@TableField("device_area_id")
	private String deviceAreaId;
	
	/**设备区域名称*/
	@ApiModelProperty(value = "设备区域名称")
	@TableField(exist = false)
	private String deviceAreaName;
}
