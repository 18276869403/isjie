package org.jeecg.modules.demo.monitor.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class DeviceDifferentVo  {



	 /**count*/
   @ApiModelProperty(value = "count")
	private Integer count;
	/** 设备类型名称 */
	@ApiModelProperty(value = "设备类型名称")
	private String deviceType;

}
