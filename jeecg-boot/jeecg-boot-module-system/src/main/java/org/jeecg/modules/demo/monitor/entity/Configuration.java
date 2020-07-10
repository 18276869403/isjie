package org.jeecg.modules.demo.monitor.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 系统配置表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Data
@TableName("configuration")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="configuration对象", description="系统配置表")
public class Configuration {

	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**监控数据保留x月	*/
	private Integer monitorDataMonth;
	/**报警数据保留x月	*/
	private Integer alarmDataMonth;
	private String createBy;
	private String updateBy;
	private Date updateTime;
	private Date createTime;
}
