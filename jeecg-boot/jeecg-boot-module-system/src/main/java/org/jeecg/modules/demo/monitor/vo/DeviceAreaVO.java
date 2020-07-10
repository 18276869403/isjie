package org.jeecg.modules.demo.monitor.vo;

import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 设备区域表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="设备区域表", description="设备区域表")
public class DeviceAreaVO {
    
	/**id*/
    @ApiModelProperty(value = "id")
	private String id;
	/**客户ID*/
    @ApiModelProperty(value = "客户ID")
	@TableField("customer_id")
	private Integer customerId;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
	@ApiModelProperty(value = "客户名称")
	private String customerName;
	/**区域名称*/
	@Excel(name = "区域名称", width = 15)
    @ApiModelProperty(value = "区域名称")
	@TableField("area_name")
	private String areaName;
	/**创建时间*/
	@Excel(name = "创建时间", width = 15)
    @ApiModelProperty(value = "创建时间")
	@TableField("create_time")
	private Date createTime;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	@TableField("create_by")
	private String createBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 15)
    @ApiModelProperty(value = "修改时间")
	@TableField("update_time")
	private Date updateTime;
	
	
	private Integer OneAreaId;
	
	private Integer twoAreaId;
}
