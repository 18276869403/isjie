package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AreaVo {

	@ApiModelProperty(value = "id")
	private Integer id;
	/** 一级区域名 */
	@ApiModelProperty(value = "一级区域名")
	private String oneAreaName;
	/** 二级区域名 */
	@ApiModelProperty(value = "二级区域名")
	private String twoAreaName;
	/** 一级区域名 */
	@ApiModelProperty(value = "一级区域名id")
	private Integer oneAreaId;
	/** 二级区域名 */
	@ApiModelProperty(value = "二级区域名id")
	private Integer twoAreaId;
	/** 一级父级编号 */
	@ApiModelProperty(value = "一级父级编号")
	private Integer onenum;
	/** 二级父级编号 */
	@ApiModelProperty(value = "二级父级编号")
	private Integer num;
	/**用户Id*/
	@ApiModelProperty(value = "用户Id")
	private String userId;
	/**客户Id*/
	@Excel(name = "客户Id", width = 15)
	@ApiModelProperty(value = "客户Id")
	private Integer customerId;
	/**客户名*/
	@Excel(name = "客户名", width = 15)
	@ApiModelProperty(value = "客户名")
	private String name;
	/** 省市名 */
	@ApiModelProperty(value = "省市名")
	private String areaName;

}
