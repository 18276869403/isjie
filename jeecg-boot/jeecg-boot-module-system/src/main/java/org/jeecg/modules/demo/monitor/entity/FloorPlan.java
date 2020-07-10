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
 * @Description: 楼层图表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Data
@TableName("floor_plan")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="floor_plan对象", description="楼层图表")
public class FloorPlan {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
    @ApiModelProperty(value = "客户id")
	private Integer customerId;
	/**楼层名*/
	@Excel(name = "楼层名", width = 15)
    @ApiModelProperty(value = "楼层名")
	private String floorName;
	/**平面图*/
	@Excel(name = "平面图", width = 15)
    @ApiModelProperty(value = "平面图")
	private String planPic;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private Object remark;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**删除状态(0-正常,1-已删除)*/
	@Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	private Integer delFlag;
	/**备用字段1*/
	@Excel(name = "备用字段1", width = 15)
    @ApiModelProperty(value = "备用字段1")
	private Integer backup1;
	/**备用字段2*/
	@Excel(name = "备用字段2", width = 15)
    @ApiModelProperty(value = "备用字段2")
	private String backup2;
	/**备用字段3*/
	@Excel(name = "备用字段3", width = 15)
    @ApiModelProperty(value = "备用字段3")
	private String backup3;
	/**备用字段4*/
	@Excel(name = "备用字段4", width = 15)
    @ApiModelProperty(value = "备用字段4")
	private String backup4;
	/**备用字段5*/
	@Excel(name = "备用字段5", width = 15)
    @ApiModelProperty(value = "备用字段5")
	private String backup5;
}
