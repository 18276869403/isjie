package org.jeecg.modules.demo.monitor.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 报警短信表
 * @Author: jeecg-boot
 * @Date:   2020-01-13
 * @Version: V1.0
 */
@Data
@TableName("alarm_sms")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="alarm_sms对象", description="报警短信表")
public class AlarmSms {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**设备iccid*/
	@Excel(name = "设备iccid", width = 15)
    @ApiModelProperty(value = "设备iccid")
	private String iccid;
	/**设备imei*/
	@Excel(name = "设备imei", width = 15)
    @ApiModelProperty(value = "设备imei")
	private String imei;
	/**设备类型*/
	@Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
	private String equipType;
	/**设备address*/
	@Excel(name = "设备address", width = 15)
    @ApiModelProperty(value = "设备address")
	private String equipAdd;
	/**短信是否发送*/
	@Excel(name = "短信是否发送", width = 15)
    @ApiModelProperty(value = "短信是否发送")
	private String smsType;
	/**报警类型*/
	@Excel(name = "报警类型", width = 15)
    @ApiModelProperty(value = "报警类型")
	private String alarmType;
	/**报警时间*/
	@Excel(name = "报警时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "报警时间")
	private Date alarmDate;
	/**发送时间*/
	@Excel(name = "发送时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发送时间")
	private Date sendDate;
	/**backup1*/
	@Excel(name = "backup1", width = 15)
    @ApiModelProperty(value = "backup1")
	private String backup1;
	/**backup2*/
	@Excel(name = "backup2", width = 15)
    @ApiModelProperty(value = "backup2")
	private String backup2;
	/**backup3*/
	@Excel(name = "backup3", width = 15)
    @ApiModelProperty(value = "backup3")
	private String backup3;

	private Integer deviceId;
	private Integer customerId;
}
