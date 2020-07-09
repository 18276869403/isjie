package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceVo {
	@ApiModelProperty(value = "id")
	private Integer id;
	@ApiModelProperty(value = "用户id")
	private Integer userId;
	/** 一级区域名 */
	@ApiModelProperty(value = "一级区域名")
	private String oneAreaName;
	/** 二级区域名 */
	@ApiModelProperty(value = "二级区域名")
	private String twoAreaName;
	/** 一级区域ID */
	@ApiModelProperty(value = "一级区域ID")
	private Integer oneAreaId;
	/** 二级区域ID */
	@ApiModelProperty(value = "一级区域ID")
	private Integer twoAreaId;
	/** 客户名 */
	@Excel(name = "客户名", width = 15)
	@ApiModelProperty(value = "客户名")
	private String name;
	/** 设备总数 */
	@ApiModelProperty(value = "设备总数")
	private Integer count;
	/** 楼层设备总数 */
	@ApiModelProperty(value = "设备总数")
	private Integer florcount;
	/** 客户ID */
	@ApiModelProperty(value = "客户ID")
	private Integer customerId;
	/** 设备类型名称 */
	@Excel(name = "设备类型名称", width = 15)
	@ApiModelProperty(value = "设备类型名称")
	private String deviceType;
	/** 设备IMEI */
	@Excel(name = "设备IMEI", width = 15)
	@ApiModelProperty(value = "设备IMEI")
	private String deviceImei;
	/** 设备位号 */
//	@Excel(name = "设备位号", width = 15)
	@ApiModelProperty(value = "设备位号")
	private String devicePositionnum;
	/** ICCID */
	@Excel(name = "ICCID", width = 15)
	@ApiModelProperty(value = "ICCID")
	private String iccid;

	/**设备报警类型*/
	@Excel(name = "设备状态", width = 15)
    @ApiModelProperty(value = "设备状态")
	private String statusType;

	/** 设备编号 */
	@Excel(name = "设备编号", width = 15)
	@ApiModelProperty(value = "设备编号")
	private String deviceNum;

	/** 设备编号/安装位置组合 */
//	@Excel(name = "设备编号", width = 15)
	@ApiModelProperty(value = "设备编号")
	private String deviceNum2;

	/** 地址编号 */
	@Excel(name = "设备ADD/485地址", width = 15)
	@ApiModelProperty(value = "设备ADD/485地址")
	private String addressNumber;
				 
	/** 设备型号 */
	@Excel(name = "设备型号", width = 15)
	@ApiModelProperty(value = "设备型号")
	private String deviceModel;
	/** 出厂编号 */
	@Excel(name = "出厂编号", width = 15)
	@ApiModelProperty(value = "出厂编号")
	private String factoryNum;
	/** 检测目标 */
	@Excel(name = "检测目标", width = 15)
	@ApiModelProperty(value = "检测目标")
	private String detectionTarget;
	/** 量程 */
	@Excel(name = "量程", width = 15)
	@ApiModelProperty(value = "量程")
	private java.math.BigDecimal ranges;
	/** 量程 */
	@ApiModelProperty(value = "拼接的量程")
	private java.lang.String ranges1;
	/** 单位 */
	@Excel(name = "单位", width = 15)
	@ApiModelProperty(value = "单位")
	private String unit;
	/** 换算系数 */
	@Excel(name = "换算系数", width = 15)
	@ApiModelProperty(value = "换算系数")
	private java.math.BigDecimal reductionCoefficient;
	/** 安装位置 */
	@Excel(name = "安装位置", width = 15)
	@ApiModelProperty(value = "安装位置")
	private String installationPosition;
	/**检测值超标报警值*/
//	@Excel(name = "检测值超标报警值", width = 15)
	@ApiModelProperty(value = "检测值超标报警值")
	private java.math.BigDecimal exceedingAlarm;
	/** 备注 */
	@Excel(name = "备注", width = 15)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;
	/**删除状态(0-正常,1-已删除)*/
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	private Integer delFlag;
	/**备用字段1*/
    @ApiModelProperty(value = "备用字段1")
	private String backup1;
	/**备用字段2*/
    @ApiModelProperty(value = "备用字段2")
	private String backup2;
	/**备用字段3*/
    @ApiModelProperty(value = "备用字段3")
	private String backup3;
	/**备用字段4*/
    @ApiModelProperty(value = "备用字段4")
	private String backup4;
	/**备用字段5*/
    @ApiModelProperty(value = "备用字段5")
	private String backup5;
    
	/**设备区域Id*/
	@ApiModelProperty(value = "设备区域Id")
	@TableField("device_area_id")
	private String deviceAreaId;
	
	/**设备区域名称*/
	@ApiModelProperty(value = "设备区域")
	@Excel(name = "设备区域", width = 15)
	private String deviceAreaName;
}
