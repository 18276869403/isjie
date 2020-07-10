package org.jeecg.modules.demo.monitor.entity;

import java.util.Date;

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
 * @Description: 监测记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Data
@TableName("test_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="test_record对象", description="监测记录表")
public class TestRecord {

	/**id*/
	@TableId(type = IdType.ID_WORKER_STR)
	@ApiModelProperty(value = "id")
	private Integer id;
	/**采集时间*/
	@Excel(name = "采集时间", width = 20, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@ApiModelProperty(value = "采集时间")
	@TableField("Acquisition_time")
	private Date acquisitionTime;
	/**检测的设备类型*/
	@Excel(name = "检测的设备类型", width = 15)
	@ApiModelProperty(value = "检测的设备类型")
	@TableField("Test_deviceType")
	private String testDeviceType;
	/**状态*/
	@Excel(name = "状态", width = 15)
	@ApiModelProperty(value = "状态")
	@TableField("T_status")
	private String tStatus;
	/**检测值*/
	@Excel(name = "检测值", width = 15)
	@ApiModelProperty(value = "检测值")
	@TableField("Test_pv")
	private java.math.BigDecimal testPv;
	
	/**设备地址信息(对应设备表的Address_number)*/
	@Excel(name = "设备地址信息", width = 15)
	@ApiModelProperty(value = "设备地址信息")
	@TableField("Test_address")
	private String testAddress;
	
	/**对应设备表里的iccid*/
	@Excel(name = "ICCID", width = 15)
	@ApiModelProperty(value = "ICCID")
	@TableField("Test_iCCID")
	private String testICCID;
	
	/**检测设备imei(对应设备表的imei)*/
	@Excel(name = "IMEI", width = 15)
	@ApiModelProperty(value = "IMEI")
	@TableField("Test_deviceImei")
	private String testDeviceImei;
	
	/**原始记录*/
	@Excel(name = "原始记录", width = 15)
	@ApiModelProperty(value = "原始记录")
	@TableField("Test_originalRecord")
	private String testOriginalRecord;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	@TableField("create_by")
	private String createBy;
	
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	@TableField("create_time")
	private Date createTime;
	
	/**修改人*/
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	@TableField("update_by")
	private String updateBy;
	
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	@TableField("update_time")
	private Date updateTime;
	
	/**删除状态(0-正常,1-已删除)*/
	@Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
	@ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	@TableField("del_flag")
	private Integer delFlag;
	
	/**备用字段1*/
	@Excel(name = "备用字段1", width = 15)
	@ApiModelProperty(value = "备用字段1")
	private String backup1;
	/**备用字段2*/
	@Excel(name = "备用字段2", width = 15)
	@ApiModelProperty(value = "备用字段2")
	private String backup2;
	/**备用字段3*/
	@Excel(name = "备用字段3", width = 15)
	@ApiModelProperty(value = "备用字段3")
	private String backup3;
	/**备用字段4*/
	@Excel(name = "备用字段4", width = 15)
	@ApiModelProperty(value = "备用字段4")
	private String backup4;
	/**备用字段5*/
	@Excel(name = "备用字段5", width = 15)
	@ApiModelProperty(value = "备用字段5")
	private String backup5;
	

    /**一级区域ID*/
	@ApiModelProperty(value = "一级区域ID")
	@TableField("oneAreaId")
	private Integer oneAreaId;
	/**二级区域Id*/
	@ApiModelProperty(value = "二级区域Id")
	@TableField("twoAreaId")
	private Integer twoAreaId;
	/** 设备id */
	@ApiModelProperty(value = "设备id")
	@TableField("deviceId")
	private Integer deviceId;
	/**id*/
	@ApiModelProperty(value = "客户id")
	@TableField(exist = false)
	private  java.lang.Integer customerId;
	/**输出模块状态*/
	@ApiModelProperty(value = "输出模块状态")
	@TableField("out_status")
	private  java.lang.String outStatus;
}
