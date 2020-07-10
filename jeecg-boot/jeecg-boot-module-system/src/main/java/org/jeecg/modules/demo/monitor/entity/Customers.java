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
 * @Description: 客户表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Data
@TableName("customers")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="customers对象", description="客户表")
public class Customers {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**一级区域ID*/
	@Excel(name = "一级区域ID", width = 15)
    @ApiModelProperty(value = "一级区域ID")
	private Integer oneAreaId;
	/**二级区域Id*/
	@Excel(name = "二级区域Id", width = 15)
    @ApiModelProperty(value = "二级区域Id")
	private Integer twoAreaId;
	/**客户名*/
	@Excel(name = "客户名", width = 15)
    @ApiModelProperty(value = "客户名")
	private String name;
	/**项目名*/
	@Excel(name = "项目名", width = 15)
    @ApiModelProperty(value = "项目名")
	private String projectName;
	/**客户地址*/
	@Excel(name = "客户地址", width = 15)
    @ApiModelProperty(value = "客户地址")
	private String detailedAddress;
	/**经度*/
	@Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
	private java.math.BigDecimal longitude;
	/**纬度*/
	@Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
	private java.math.BigDecimal latitude;
	/**客户logo*/
	@Excel(name = "客户logo", width = 15)
    @ApiModelProperty(value = "客户logo")
	private String custLogo;
	/**标注logo*/
	@Excel(name = "客户logo", width = 15)
	@ApiModelProperty(value = "客户logo")
	private String biaoZhuLogo;
	/**客户web端显示名称*/
	@Excel(name = "客户web端显示名称", width = 15)
    @ApiModelProperty(value = "客户web端显示名称")
	private String webName;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**用户添加时间*/
	@Excel(name = "用户添加时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "用户添加时间")
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
}
