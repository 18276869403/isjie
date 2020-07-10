package org.jeecg.modules.demo.monitor.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class LoadCustomersVo {
	/**客户Id*/
	@ApiModelProperty(value = "客户Id")
	private Integer id;
	/**客户名*/
	@ApiModelProperty(value = "客户名")
	private String name;
	/**客户名*/
	@ApiModelProperty(value = "项目名")
	private String projectName;
	/**经度*/
	@ApiModelProperty(value = "经度")
	private java.math.BigDecimal longitude;
	/**纬度*/
	@ApiModelProperty(value = "纬度")
	private java.math.BigDecimal latitude;
	/**客户logo*/
	private String custLogo;
	/**客户logo*/
	private String logo;
	/**客户设备数量*/
	private Integer count;
	/*省名*/
	private String oneAreaName;
	/*市名*/
	private String twoAreaName;
}
