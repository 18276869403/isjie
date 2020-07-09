package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 客户列表信息
 * @Author: jeecg-boot
 * @Date:   2019-12-23
 * @Version: V1.0
 */
@Data
public class CustomersVo {

	/**id*/
    @ApiModelProperty(value = "id")
	private Integer id;
	/**一级区域ID*/
    @ApiModelProperty(value = "一级区域ID")
	private Integer oneAreaId;
	/**二级区域Id*/
    @ApiModelProperty(value = "二级区域Id")
	private Integer twoAreaId;
	/**一级区域名*/
    @ApiModelProperty(value = "一级区域名")
	private String oneAreaName;
	/**二级区域名*/
    @ApiModelProperty(value = "二级区域名")
	private String twoAreaName;
	/**客户名*/
    @ApiModelProperty(value = "客户名")
	private String name;
	/**项目名*/
    @ApiModelProperty(value = "项目名")
	private String projectName;
	/**客户地址*/
    @ApiModelProperty(value = "客户地址")
	private String detailedAddress;
	/**经度*/
    @ApiModelProperty(value = "经度")
	private java.math.BigDecimal longitude;
	/**纬度*/
    @ApiModelProperty(value = "纬度")
	private java.math.BigDecimal latitude;
	/**客户logo*/
    @ApiModelProperty(value = "客户logo")
	private String custLogo;
	/**标注logo*/
	@Excel(name = "标注logo", width = 15)
	@ApiModelProperty(value = "标注logo")
	private String biaoZhuLogo;
	/**客户web端显示名称*/
    @ApiModelProperty(value = "客户web端显示名称")
	private String webName;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**用户添加时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "用户添加时间")
	private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;
	/**删除状态(0-正常,1-已删除)*/
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	private Integer delFlag;
}
