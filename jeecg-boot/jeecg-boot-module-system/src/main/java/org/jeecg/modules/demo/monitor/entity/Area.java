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
 * @Description: 省市表
 * @Author: jeecg-boot
 * @Date:   2020-01-03
 * @Version: V1.0
 */
@Data
@TableName("area")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="area对象", description="省市表")
public class Area {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**省市名*/
	@Excel(name = "省市名", width = 15)
    @ApiModelProperty(value = "省市名")
	private String areaName;
	/**父级编号*/
	@Excel(name = "父级编号", width = 15)
    @ApiModelProperty(value = "父级编号")
	private Integer num;
	/**createBy*/
	@Excel(name = "createBy", width = 15)
    @ApiModelProperty(value = "createBy")
	private String createBy;
	/**createTime*/
	@Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
	private Date createTime;
	/**updateBy*/
	@Excel(name = "updateBy", width = 15)
    @ApiModelProperty(value = "updateBy")
	private String updateBy;
	/**updateTime*/
	@Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateTime")
	private Date updateTime;
	/**delFlag*/
	@Excel(name = "delFlag", width = 15)
    @ApiModelProperty(value = "delFlag")
	private Integer delFlag;
	/**backup1*/
	@Excel(name = "backup1", width = 15)
    @ApiModelProperty(value = "backup1")
	private String backup1;
	/**backup2*/
	@Excel(name = "backup2", width = 15)
    @ApiModelProperty(value = "backup2")
	private String backup2;
	/**backup3*/
	@Excel(name = "backup3", width = 15)
    @ApiModelProperty(value = "backup3")
	private String backup3;
	/**backup4*/
	@Excel(name = "backup4", width = 15)
    @ApiModelProperty(value = "backup4")
	private String backup4;
	/**backup5*/
	@Excel(name = "backup5", width = 15)
    @ApiModelProperty(value = "backup5")
	private String backup5;
}
