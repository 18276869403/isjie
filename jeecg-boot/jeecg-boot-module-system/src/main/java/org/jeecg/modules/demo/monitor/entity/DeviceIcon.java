package org.jeecg.modules.demo.monitor.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 设备图标表
 * @Author: jeecg-boot
 * @Date: 2019-12-23
 * @Version: V1.0
 */
@Data
@TableName("device_icon")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "device_icon对象", description = "设备图标表")
public class DeviceIcon {

	/** id */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "id")
	private Integer id;
	/** 设备类型名称(探测器，控制器，输出模块) */
	@Excel(name = "设备类型名称(探测器，控制器，输出模块)", width = 15)
	@ApiModelProperty(value = "设备类型名称(探测器，控制器，输出模块)")
	private String deviceType;
	/** 图标图片 */
	@Excel(name = "图标图片", width = 15)
	@ApiModelProperty(value = "图标图片")
	private String iconPic;
	/** 图标类型(正常，维护，低/高警，故障，通讯故障) */
	@Excel(name = "图标类型(正常，维护，低/高警，故障，通讯故障)", width = 15)
	@ApiModelProperty(value = "图标类型(正常，维护，低/高警，故障，通讯故障)")
	private String iconType;
	/** 备注 */
	@Excel(name = "备注", width = 15)
	@ApiModelProperty(value = "备注")
	private Object remark;
	/** 创建人 */
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;
	/** 创建时间 */
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	/** 修改人 */
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private String updateBy;
	/** 修改时间 */
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/** 删除状态(0-正常,1-已删除) */
	@Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
	@ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	private Integer delFlag;
	/** 备用字段1 */
	@Excel(name = "备用字段1", width = 15)
	@ApiModelProperty(value = "备用字段1")
	private String backup1;
	/** 备用字段2 */
	@Excel(name = "备用字段2", width = 15)
	@ApiModelProperty(value = "备用字段2")
	private String backup2;
	/** 备用字段3 */
	@Excel(name = "备用字段3", width = 15)
	@ApiModelProperty(value = "备用字段3")
	private String backup3;
	/** BackUp4 */
	@Excel(name = "BackUp4", width = 15)
	@ApiModelProperty(value = "BackUp4")
	private String backup4;
	/** 备用字段5 */
	@Excel(name = "备用字段5", width = 15)
	@ApiModelProperty(value = "备用字段5")
	private String backup5;
}
