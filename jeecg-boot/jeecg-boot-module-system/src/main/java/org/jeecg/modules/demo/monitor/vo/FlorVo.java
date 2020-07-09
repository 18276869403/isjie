package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FlorVo {
	@ApiModelProperty(value = "id")
	private Integer id;
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
	@ApiModelProperty(value = "客户名")
	private String name;
	/** 客户ID */
	@ApiModelProperty(value = "客户ID")
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
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;
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
