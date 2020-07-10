package org.jeecg.modules.demo.monitor.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 客户账号列表信息
 * @Author: jeecg-boot
 * @Date:   2019-12-24
 * @Version: V1.0
 */
@Data
public class CustomersAccountsVo {

	/**id*/
    @ApiModelProperty(value = "id")
	private Integer id;
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
    /**客户Id*/
    @ApiModelProperty(value = "客户Id")
	private Integer customerId;
	/**客户名*/
    @ApiModelProperty(value = "客户名")
	private String name;
	/**项目名*/
    @ApiModelProperty(value = "项目名")
	private String projectName;
    /**账户名称*/
    @ApiModelProperty(value = "账户名称")
	private String account;
	/**账户密码*/
    @ApiModelProperty(value = "账户密码")
	private String password;
	/**接受报警短信1-接受，2-拒绝*/
    @ApiModelProperty(value = "接受报警短信1-接受，2-拒绝")
	private Integer shortLetter;
	/**接受手机号(接收报警短信时，填手机号)*/
    @ApiModelProperty(value = "接受手机号(接收报警短信时，填手机号)")
	private String phone;
	/**备注*/
    @ApiModelProperty(value = "备注")
	private Object remark;
	/**状态(1-启用，2-禁用)*/
    @ApiModelProperty(value = "状态(1-启用，2-禁用)")
	private Integer custState;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**用户添加时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "客户添加时间")
	private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;
	/**删除状态(0-正常,1-已删除)*/
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	private Integer delFlag;
}
