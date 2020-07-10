package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class DeviceWindowVo {

	/**客户设备类型数量*/
	@Excel(name = "客户设备类型数量", width = 15)
	@ApiModelProperty(value = "count")
	private Integer count;
	/**设备报警状态*/
	@Excel(name = "设备报警类型", width = 15)
    @ApiModelProperty(value = "设备报警状态")
	private String StatusType;
	
}
