package org.jeecg.modules.demo.monitor.entity;

import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 报警记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Data
@TableName("alarm_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="alarm_record对象", description="报警记录表")
public class AlarmRecord {

	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**原始记录*/
	@Excel(name = "报警时间", width = 20, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "报警时间")
	private Date alarmTime;
	/**原始记录*/
	@Excel(name = "原始记录", width = 15)
    @ApiModelProperty(value = "原始记录")
	private String alarmOriginalRecord;
	/**ICCID*/
	@Excel(name = "ICCID", width = 15)
    @ApiModelProperty(value = "ICCID")
	private String alarmICCID;
	/**设备地址信息(对应设备表的Address_number)*/
	@Excel(name = "设备地址信息", width = 15)
    @ApiModelProperty(value = "设备地址信息(对应设备表的Address_number)")
	private String alarmAddress;
	/**Type=1探测器，TYPE=2控制器，type=3输出模块,对应设备表的deviceType*/
	@Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "Type=1探测器，TYPE=2控制器，type=3输出模块,对应设备表的deviceType")
	private String alarmDeviceType;
	/**报警类型*/
	@Excel(name = "报警类型", width = 15)
    @ApiModelProperty(value = "报警类型")
	private String tstatus;
	/**浓度（检测值=对应设备的采集值x换算系数）*/
	@Excel(name = "检测值", width = 15)
    @ApiModelProperty(value = "检测值")
	private java.math.BigDecimal alarmPv;
	/**IMEI*/
	@Excel(name = "IMEI", width = 15)
    @ApiModelProperty(value = "IMEI")
	private String alarmDeviceImei;
	/**报警状态0：触发报警 1、解除报警*/
	@Excel(name = "报警状态0：触发报警 1、解除报警", width = 15)
    @ApiModelProperty(value = "报警状态0：触发报警 1、解除报警")
	private Integer alarmState;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**删除状态(0-正常,1-已删除)*/
	@Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	private Integer delFlag;
	/**备用字段1*/
	@Excel(name = "备用字段1", width = 15)
    @ApiModelProperty(value = "备用字段1：通讯是否恢复标识")
	private String backup1;
	/**备用字段2*/
	@Excel(name = "备用字段2", width = 15)
    @ApiModelProperty(value = "备用字段2：设备id")
	private Integer backup2;
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
}
