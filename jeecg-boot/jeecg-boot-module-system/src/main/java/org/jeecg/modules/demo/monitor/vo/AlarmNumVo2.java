package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlarmNumVo2 {
	
	/** 数量 */
	@ApiModelProperty(value = "数量")
	private String count;
	/**IMEI*/
	@Excel(name = "IMEI", width = 15)
    @ApiModelProperty(value = "IMEI")
	private String alarmDeviceImei;
	/**设备地址信息(对应设备表的Address_number)*/
	@Excel(name = "设备地址信息", width = 15)
    @ApiModelProperty(value = "设备地址信息(对应设备表的Address_number)")
	private String alarmAddress;
	@ApiModelProperty(value = "设备ICCID")
	private String alarmICCID;
	@ApiModelProperty(value = "设备类型")
	private String alarmDeviceType;
	@ApiModelProperty(value = "设备编号")
	private String deviceNum;
	
	@ApiModelProperty(value = "设备id")
	private Integer deviceId;
}
