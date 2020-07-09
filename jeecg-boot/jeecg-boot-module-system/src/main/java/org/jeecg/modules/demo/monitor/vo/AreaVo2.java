package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AreaVo2 {

	@ApiModelProperty(value = "id")
	private Integer id;
	/**客户名*/
	@Excel(name = "客户名", width = 15)
	@ApiModelProperty(value = "客户名")
	private String name;
	/** 省市名 */
	@ApiModelProperty(value = "省市名")
	private String areaName;

}
