package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlarmNumVo {
	
	/** 时间 */
	@ApiModelProperty(value = "时间")
	private String days;
	/** 数量 */
	@ApiModelProperty(value = "数量")
	private String count;
	
}
