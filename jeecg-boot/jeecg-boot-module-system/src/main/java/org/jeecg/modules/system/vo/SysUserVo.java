package org.jeecg.modules.system.vo;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
public class SysUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "id")
    private String id;

    /**
     * 登录账号
     */
    @Excel(name = "登录账号", width = 15)
    @ApiModelProperty(value = "登录账号")
    private String username;

    /**
     * 真实姓名
     */
    @Excel(name = "真实姓名", width = 15)
    @ApiModelProperty(value = "真实姓名")
    private String realname;
    /**
     *	 角色名称
     */
    @ApiModelProperty(value = "用户角色名称")
    private String roleName;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * md5密码盐
     */
    @ApiModelProperty(value = "md5密码盐")
    private String salt;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 生日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "生日")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    @Dict(dicCode = "sex")
    @ApiModelProperty(value = "性别")
    private Integer sex;

    /**
     * 电子邮件
     */
    @ApiModelProperty(value = "电子邮件")
    private String email;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String notes;
    /**
     * 部门code
     */
    @ApiModelProperty(value = "部门code")
    private String orgCode;

    /**
     * 状态(1：正常  2：冻结 ）
     */
    @Dict(dicCode = "user_status")
    @ApiModelProperty(value = "状态")
    private Integer status;
    
    /**
     * 是否接收短信
     */
    @ApiModelProperty(value = "是否接收短信")
    private Integer receiveSms;
    
    
    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    @ApiModelProperty(value = "删除状态")
    private String delFlag;

    /**
     * 工号，唯一键
     */
    @ApiModelProperty(value = "工号，唯一键")
    private String workNo;

    /**
     * 职务，关联职务表
     */
    @ApiModelProperty(value = "职务，关联职务表")
    private String post;

    /**
     * 座机号
     */
    @ApiModelProperty(value = "座机号")
    private String telephone;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**
     * 同步工作流引擎1同步0不同步
     */
    @ApiModelProperty(value = "同步工作流引擎1同步0不同步")
    private String activitiSync;


}
