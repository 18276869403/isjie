package org.jeecg.modules.demo.monitor.vo;

import java.util.Date;

import org.jeecg.modules.demo.monitor.entity.Device;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CaiJiVO {
    private String imei;
    private String iccid;
    private String add;
    private String status;
    private String rtStatus;
    private String statusBit;
    private String pv;
    private Date time;
    /**一级区域ID*/
	@ApiModelProperty(value = "一级区域ID")
	private Integer oneAreaId;
	/**二级区域Id*/
	@ApiModelProperty(value = "二级区域Id")
	private Integer twoAreaId;
	/** 设备id */
	@ApiModelProperty(value = "设备id")
	private Integer deviceId;
	/**id*/
	@ApiModelProperty(value = "客户id")
	private  java.lang.Integer customerId;

	@ApiModelProperty(value = "数据类型")
	private  java.lang.String type;

	@ApiModelProperty(value = "设备时间")
	private  java.lang.String dataTime;

	private Device device;
	/**输出模块状态*/
	@ApiModelProperty(value = "输出模块状态")
	private  java.lang.String outStatus;
}
