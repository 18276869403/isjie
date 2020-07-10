package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TestRecordVo {
	//检测表
	/**id*/
	@ApiModelProperty(value = "id")
	private java.lang.Integer id;

	/**采集时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "采集时间")
	private java.util.Date acquisitionTime;

	/**Type=1探测器，TYPE=2控制器，type=3输出模块,对应设备表的deviceType*/
	@ApiModelProperty(value = "Type=1探测器，TYPE=2控制器，type=3输出模块,对应设备表的deviceType")
	private java.lang.String testDevicetype;

	/**状态类型：正常，故障，通讯故障，低警，高警，主电欠压,备电欠压,维护*/
	@ApiModelProperty(value = "状态类型：正常，故障，通讯故障，低警，高警，主电欠压,备电欠压,维护")
	private java.lang.String tStatus;

	/**浓度（检测值=对应设备的采集值x换算系数）*/
	@ApiModelProperty(value = "浓度（检测值=对应设备的采集值x换算系数）")
	private java.lang.String testPv;

	/**设备地址信息(对应设备表的Address_number)*/
	@ApiModelProperty(value = "设备地址信息(对应设备表的Address_number)")
	private java.lang.String testAddress;

	/**对应设备表里的iccid*/
	@ApiModelProperty(value = "对应设备表里的iccid")
	private java.lang.String testIccid;
	/**检测设备imei(对应设备表的imei)*/
	@ApiModelProperty(value = "检测设备imei(对应设备表的imei)")
	private java.lang.String testDeviceimei;

	/**原始记录*/
	@ApiModelProperty(value = "原始记录")
	private java.lang.String testOriginalrecord;

	//客户Vo
	/**一级区域ID*/
	@ApiModelProperty(value = "一级区域ID")
	private Integer oneAreaId;
	/**二级区域Id*/
	@ApiModelProperty(value = "二级区域Id")
	private Integer twoAreaId;
	/**一级区域名*/
	@ApiModelProperty(value = "一级区域名")
	private String oneAreaName;
	/**二级区域名*/
	@ApiModelProperty(value = "二级区域名")
	private String twoAreaName;
	/**客户名*/
	@ApiModelProperty(value = "客户名")
	private String name;
	/**项目名*/
	@ApiModelProperty(value = "项目名")
	private String projectName;

	//设备vo
	/** 设备类型名称 */
	@ApiModelProperty(value = "设备类型名称")
	private String deviceType;
	/** 设备IMEI */
	@ApiModelProperty(value = "设备IMEI")
	private String deviceImei;
	/** 检测目标 */
	@ApiModelProperty(value = "检测目标")
	private String detectionTarget;
	/** 量程 */
	@ApiModelProperty(value = "量程")
	private java.lang.String ranges;
	/** 量程 */
	@ApiModelProperty(value = "拼接的量程")
	private java.lang.String ranges1;
	/** 设备编号 */
	@ApiModelProperty(value = "设备编号")
	private String deviceNum;
	/** 设备id */
	@ApiModelProperty(value = "设备id")
	private Integer deviceId;

	/**楼层名*/
	@ApiModelProperty(value = "楼层名")
	private String floorName;

	//检测表
	/**id*/
	@ApiModelProperty(value = "userId")
	private String userId;
	/**id*/
	@ApiModelProperty(value = "customerId")
	private  java.lang.Integer customerId;
	/** 设备型号 */
	@Excel(name = "设备型号", width = 15)
	@ApiModelProperty(value = "设备型号")
	private String deviceModel;

	/** 单位 */
	@Excel(name = "单位", width = 15)
	@ApiModelProperty(value = "单位")
	private String unit;

}
