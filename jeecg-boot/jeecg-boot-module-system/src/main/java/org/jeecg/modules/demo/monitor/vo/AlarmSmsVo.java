package org.jeecg.modules.demo.monitor.vo;

import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class AlarmSmsVo {
	//自定义报警实体
	  
	/**自定义主键id*/
	@ApiModelProperty(value = "id")
	private Integer id;
		
	/**报警实体类中*/
	/**报警时间*/
	@Excel(name = "报警时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "报警时间")
	private java.util.Date alarmTime;
	/**状态*/
	@Excel(name = "报警类型", width = 15)
    @ApiModelProperty(value = "状态")
	private String tstatus;
	/**省市表*/
	/**省份名*/
	@Excel(name = "省份", width = 15)
    @ApiModelProperty(value = "省份")
	private String oneAreaName;
    /**省份ID*/
    @ApiModelProperty(value = "省份ID")
	private Integer oneAreaId;
	/**城市ID*/
    @ApiModelProperty(value = "城市ID")
	private Integer twoAreaId;
	/**城市*/
    @Excel(name = "城市", width = 15)
    @ApiModelProperty(value = "城市")
	private String twoAreaName;
    /**客户ID*/
    @ApiModelProperty(value = "客户ID")
	private Integer customerId;
    /**客户实体类中*/
	/**客户名*/
    @ApiModelProperty(value = "客户名")
	private String name;
	/**项目名*/
    @ApiModelProperty(value = "项目名")
	private String projectName;
	/**设备IMEI*/
    @ApiModelProperty(value = "设备IMEI")
	private String alarmDeviceImei;
	/**检测目标*/
    @ApiModelProperty(value = "检测目标")
	private String detectionTarget;
	/**设备地址*/
    @ApiModelProperty(value = "设备地址")
	private String alarmAddress;

	/**设备类型名称*/

    @ApiModelProperty(value = "设备类型名称")
	private String alarmDeviceType;
	/**设备编号*/
    @ApiModelProperty(value = "设备编号")
	private String deviceNum;
    /**设备位号*/
    @ApiModelProperty(value = "设备位号")
	private String devicePositionnum;

    /**设备iccid*/
    @ApiModelProperty(value = "设备iccid")
	private String iccid;
	/**设备imei*/
    @ApiModelProperty(value = "设备imei")
	private String imei;
	/**设备类型*/
    @ApiModelProperty(value = "设备类型")
	private String equipType;
	/**设备address*/
    @ApiModelProperty(value = "设备address")
	private String equipAdd;
	/**短信是否发送*/
    @ApiModelProperty(value = "短信是否发送")
	private String smsType;
	/**报警类型*/
    @ApiModelProperty(value = "报警类型")
	private String alarmType;
	/**报警时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "报警时间")
	private java.util.Date alarmDate;
	/**发送时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "发送时间")
	private java.util.Date sendDate;

	@ApiModelProperty(value = "发送手机号")
	private String backUP1;
}
