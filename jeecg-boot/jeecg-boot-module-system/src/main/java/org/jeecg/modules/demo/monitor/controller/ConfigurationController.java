package org.jeecg.modules.demo.monitor.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.monitor.entity.Configuration;
import org.jeecg.modules.demo.monitor.service.IConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.ApiOperation;

/**
 * 系统配置
 * @author Jack
 *
 */
@RestController
@EnableAsync
@RequestMapping("/monitor/conf")
public class ConfigurationController {
	@Autowired
	private IConfigurationService configurationService;
	/**
	 * 分页列表查询
	 *
	 * @param device
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value = "配置分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(Configuration configuration, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<Configuration> queryWrapper = QueryGenerator.initQueryWrapper(configuration, req.getParameterMap());
		Page<Configuration> page = new Page<Configuration>(pageNo, pageSize);
		IPage<Configuration> pageList = configurationService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	@ApiOperation(value = "获取配置")
	@RequestMapping(value = "/get")
	public Result<?> get() {
		QueryWrapper<Configuration> queryWrapper = new QueryWrapper<Configuration>();
		return Result.ok(this.configurationService.getOne(queryWrapper));
	}
	
	@ApiOperation(value = "修改配置")
	@RequestMapping(value = "/update")
	public Result<?> update(@RequestBody Configuration con) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if(con.getId() != null && con.getId() > 0) {
			con.setUpdateBy(user.getId());
			con.setUpdateTime(new Date());
			return Result.ok(this.configurationService.updateById(con));
		} else {
			con.setCreateBy(user.getId());
			con.setUpdateTime(new Date());
			con.setCreateTime(new Date());
			return Result.ok(this.configurationService.save(con));
		}
	}
}
