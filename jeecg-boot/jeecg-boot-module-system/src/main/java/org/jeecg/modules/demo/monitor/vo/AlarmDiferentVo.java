package org.jeecg.modules.demo.monitor.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlarmDiferentVo {
   
	 /**count 每种设备报警的数量*/
	   @ApiModelProperty(value = "count")
		private Integer count;
		/** 设备类型名称 */
		@ApiModelProperty(value = "设备类型名称")
		private String deviceType;
}
