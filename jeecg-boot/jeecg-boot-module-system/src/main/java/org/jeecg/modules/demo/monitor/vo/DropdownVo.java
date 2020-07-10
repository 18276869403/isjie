package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DropdownVo {
/** id*/
	@ApiModelProperty(value = "id")
	private Integer id;

	/** 省市名 */
	@ApiModelProperty(value = "省市名")
	private String areaName;
	
		/**客户名称*/
	@ApiModelProperty(value = "客户名")
	private String name;
}
