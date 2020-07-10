package org.jeecg.modules.demo.monitor.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.monitor.entity.Distribution;
import org.jeecg.modules.demo.monitor.mapper.DistributionMapper;
import org.jeecg.modules.demo.monitor.service.IDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 分配表
 * @Author: jeecg-boot
 * @Date: 2020-01-03
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "分配表")
@RestController
@RequestMapping("/monitor/distribution")
public class DistributionController extends JeecgController<Distribution, IDistributionService> {
	@Autowired
	private IDistributionService distributionService;
	@Autowired
	private DistributionMapper disMapper;

	/**
	 * 分页列表查询
	 *
	 * @param distribution
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "分配表-分页列表查询")
	@ApiOperation(value = "分配表-分页列表查询", notes = "分配表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(Distribution distribution,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<Distribution> queryWrapper = QueryGenerator.initQueryWrapper(distribution, req.getParameterMap());
		Page<Distribution> page = new Page<Distribution>(pageNo, pageSize);
		IPage<Distribution> pageList = distributionService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param distribution
	 * @return
	 */
	@AutoLog(value = "分配表-添加")
	@ApiOperation(value = "分配表-添加", notes = "分配表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Distribution distribution) {
		distributionService.save(distribution);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param distribution
	 * @return
	 */
	@AutoLog(value = "分配表-编辑")
	@ApiOperation(value = "分配表-编辑", notes = "分配表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Distribution distribution) {
		distributionService.updateById(distribution);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "分配表-通过id删除")
	@ApiOperation(value = "分配表-通过id删除", notes = "分配表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		distributionService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "分配表-批量删除")
	@ApiOperation(value = "分配表-批量删除", notes = "分配表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.distributionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "分配表-通过id查询")
	@ApiOperation(value = "分配表-通过id查询", notes = "分配表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		Distribution distribution = distributionService.getById(id);
		return Result.ok(distribution);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param distribution
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, Distribution distribution) {
		return super.exportXls(request, distribution, Distribution.class, "分配表");
	}

	/**
	 * 通过excel导入数据
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, Distribution.class);
	}

	// 根据用户分配客户
	@RequestMapping(value = "/queryAll", method = RequestMethod.GET)
	public Result<List<Distribution>> queryAll(@RequestParam(name = "userId", required = true) String userId) {
		Result<List<Distribution>> result = new Result<List<Distribution>>();
		List<Distribution> list = disMapper.queryAll(userId);
//	  System.out.println("--------------------dislist------------"+list);
		if (list != null && list.size() != 0) {
			for(int a = 0; a < list.size(); a++) {
				// 判断客户id是否存在区域，如果存在区域
			}
			result.setResult(list);
			result.setSuccess(true);
		} else {
			result.error500("list为空");
		}
		return result;
	}

}
