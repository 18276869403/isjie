package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class AlarmWindowVo {
	/**报警时间*/
	@Excel(name = "报警时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "报警时间")
	private java.util.Date alarmTime;
	@ApiModelProperty(value = "客户ID")
	private Integer customerId;

	@ApiModelProperty(value = "设备ID")
	private Integer deviceId;
	 
	/** 安装位置 */
	@ApiModelProperty(value = "安装位置")
	private String installationPosition;
	
	/** 设备编号 */
	@ApiModelProperty(value = "设备编号")
	private String deviceNum;
	
	/**设备报警类型*/
	@Excel(name = "设备报警类型", width = 15)
    @ApiModelProperty(value = "设备报警类型")
	private String StatusType;
	
	/**报警类型*/
	@Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
	private String deviceType;
}
