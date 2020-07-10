package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DistributionVo {
	@ApiModelProperty(value = "id")
	private Integer id;
	/** 一级区域名 */
	@ApiModelProperty(value = "一级区域名")
	private String oneAreaName;
	/** 二级区域名 */
	@ApiModelProperty(value = "二级区域名")
	private String twoAreaName;
	/** 一级区域ID */
	@ApiModelProperty(value = "一级区域ID")
	private Integer oneAreaId;
	/** 二级区域ID */
	@ApiModelProperty(value = "一级区域ID")
	private Integer twoAreaId;
	/** 客户名 */
	@ApiModelProperty(value = "客户名")
	private String name;
	/** 客户ID */
	@ApiModelProperty(value = "客户ID")
	private Integer customerId;
	/** 项目名 */
	@Excel(name = "项目名", width = 15)
	@ApiModelProperty(value = "项目名")
	private String projectName;
	/** * 真实姓名 */
	@ApiModelProperty(value = "真实姓名")
	private String realname;
}
