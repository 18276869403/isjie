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
public class AlarmRecordVo {
	//自定义报警实体
	  
	/**自定义主键id*/
	@ApiModelProperty(value = "id")
	private Integer id;

	/**用户id**/
	@ApiModelProperty(value = "用户id")
	private String userId;
	/**报警时间*/
	@Excel(name = "报警时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "报警时间")
	private java.util.Date alarmTime;

	/**设备地址*/
	@Excel(name = "设备ADD/485地址", width = 15)
	@ApiModelProperty(value = "设备ADD/485地址")
	private String alarmAddress;

//	/**报警类型*/
//	@Excel(name = "报警类型", width = 15)
//    @ApiModelProperty(value = "报警类型")
//	private java.lang.String statusType;
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

	/**客户名*/
	@Excel(name = "客户名", width = 15)
    @ApiModelProperty(value = "客户名")
	private String name;
	/**项目名*/
	@Excel(name = "项目名", width = 15)
    @ApiModelProperty(value = "项目名")
	private String projectName;
	/**设备IMEI*/
	@Excel(name = "设备IMEI", width = 15)
    @ApiModelProperty(value = "设备IMEI")
	private String alarmDeviceImei;

	/**ICCID*/
	@Excel(name = "ICCID", width = 15)
	@ApiModelProperty(value = "ICCID")
	private String alarmICCID;

	/**检测目标*/
	@Excel(name = "检测目标", width = 15)
    @ApiModelProperty(value = "检测目标")
	private String detectionTarget;
	/**量程*/
	@Excel(name = "量程", width = 15)
    @ApiModelProperty(value = "量程")
	private java.lang.String ranges;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
	private String unit;
	/**报警状态0：触发报警 1、解除报警*/
	@Excel(name = "报警状态", width = 15,dicCode="Alarm_state")
	@Dict(dicCode = "Alarm_state")
    @ApiModelProperty(value = "报警状态0：触发报警 1、解除报警")
	private String alarmState;
	/**检测值*/
	@Excel(name = "检测值", width = 15)
    @ApiModelProperty(value = "检测值")
	private java.lang.String alarmPv;
	/**设备类型名称*/
	@Excel(name = "设备类型名称", width = 15)
    @ApiModelProperty(value = "设备类型名称")
	private String alarmDeviceType;
//	/**设备类型名称*/
//	@Excel(name = "设备类型名称", width = 15)
//	@ApiModelProperty(value = "设备类型名称")
//	private String deviceType;
	/**设备编号*/
    @ApiModelProperty(value = "设备编号")
	private String deviceNum;
    /**设备位号*/
    @ApiModelProperty(value = "设备位号")
	private String devicePositionnum;
    /**楼层名*/
    @Excel(name = "楼层名", width = 15)
    @ApiModelProperty(value = "楼层名")
	private String floorName;
	/**楼层id*/
	@Excel(name = "楼层id", width = 15)
	@ApiModelProperty(value = "楼层id")
	private String floorId;
	/**设备id*/
	@Excel(name = "设备id", width = 15)
	@ApiModelProperty(value = "设备id")
	private String deviceId;
	/**设备型号*/
	@Excel(name = "设备型号", width = 15)
	@ApiModelProperty(value = "设备型号")
	private String deviceModel;
	
}
