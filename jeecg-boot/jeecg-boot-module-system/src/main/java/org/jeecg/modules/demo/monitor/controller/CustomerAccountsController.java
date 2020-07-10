package org.jeecg.modules.demo.monitor.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.monitor.entity.CustomerAccounts;
import org.jeecg.modules.demo.monitor.service.ICustomerAccountsService;
import org.jeecg.modules.demo.monitor.vo.CustomersAccountsVo;

import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.shiro.authc.ShiroRealm;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 客户账号表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags="客户账号表")
@RestController
@RequestMapping("/monitor/customerAccounts")
public class CustomerAccountsController extends JeecgController<CustomerAccounts, ICustomerAccountsService> {
	@Autowired
	private ICustomerAccountsService customerAccountsService;
	 @Autowired
	 private ISysBaseAPI sysBaseAPI;
	
	/**
	 * 分页列表查询
	 *
	 * @param customerAccounts
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "客户账号表-分页列表查询")
	@ApiOperation(value="客户账号表-分页列表查询", notes="客户账号表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CustomerAccounts customerAccounts,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CustomerAccounts> queryWrapper = QueryGenerator.initQueryWrapper(customerAccounts, req.getParameterMap());
		Page<CustomerAccounts> page = new Page<CustomerAccounts>(pageNo, pageSize);
		IPage<CustomerAccounts> pageList = customerAccountsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	/**
	 * 分页列表查询
	 *
	 * @param customerAccounts
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "客户账号列表-分页列表查询")
	@ApiOperation(value = "客户账号列表-分页列表查询", notes = "客户账号列表-分页列表查询")
	@GetMapping(value = "/queryCustAccountsVoAll")
	public Result<?> queryCustAccountsVoAll(CustomersAccountsVo customerAccounts,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<CustomersAccountsVo> queryWrapper = new QueryWrapper<CustomersAccountsVo>();
		String header = req.getHeader("X-Access-Token");
		ShiroRealm shiroRealm = new ShiroRealm();
		String username = JwtUtil.getUsername(header);
		LoginUser sysUser = sysBaseAPI.getUserByName(username);
		queryWrapper.eq("dis.User_Id",sysUser.getId());
		if (customerAccounts.getCustomerId() != null) {
			queryWrapper.eq("a.Customer_id", customerAccounts.getCustomerId());
		}
		// 一级区域查询
		if (customerAccounts.getOneAreaName() != null) {
			queryWrapper.eq("c.One_area_id", customerAccounts.getOneAreaName());
		}
		// 二级区域
		if (customerAccounts.getTwoAreaName() != null) {
			queryWrapper.eq("c.Two_area_id", customerAccounts.getTwoAreaName());
		}
		// 客户名模糊查询
		if (StrUtil.isNotBlank(customerAccounts.getName())) {
			queryWrapper.like("c.name", customerAccounts.getName());
		}
		// 账号名模糊查询
		if (StrUtil.isNotBlank(customerAccounts.getAccount())) {
			queryWrapper.like("a.Account", customerAccounts.getAccount());
		}
		Page<CustomersAccountsVo> page = new Page<CustomersAccountsVo>(pageNo, pageSize);
		IPage<CustomersAccountsVo> pageList = customerAccountsService.queryCustAccountsVoAll(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	
	/**
	 * 状态
	 *
	 * @param
	 * @return
	 */
	@AutoLog(value = "审核状态")
 	@ApiOperation(value = "审核状态", notes = "审核状态")
 	@RequestMapping(value = "/custState", method = RequestMethod.PUT)
 	public Result<CustomerAccounts> custState(@RequestBody JSONObject jsonObject) {
 		Result<CustomerAccounts> result = new Result<CustomerAccounts>();
 		try {
 			String ids = jsonObject.getString("ids");
 			String custState = jsonObject.getString("custState");
 			String[] arr = ids.split(",");
 			for (String id : arr) {
 				if (oConvertUtils.isNotEmpty(id)) {
 					this.customerAccountsService.update(new CustomerAccounts().setCustState(Integer.parseInt(custState)),
 							new UpdateWrapper<CustomerAccounts>().lambda().eq(CustomerAccounts::getId, id));
 					
 				}
 			}
 		} catch (Exception e) {
 			log.error(e.getMessage(), e);
 			result.error500("操作失败" + e.getMessage());
 		}
 		result.success("操作成功!");
 		return result;

 	}
	
	
	/**
	 * 添加
	 *
	 * @param customerAccounts
	 * @return
	 */
	@AutoLog(value = "客户账号表-添加")
	@ApiOperation(value="客户账号表-添加", notes="客户账号表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CustomerAccounts customerAccounts) {
		customerAccountsService.save(customerAccounts);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param customerAccounts
	 * @return
	 */
	@AutoLog(value = "客户账号表-编辑")
	@ApiOperation(value="客户账号表-编辑", notes="客户账号表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CustomerAccounts customerAccounts) {
		customerAccountsService.updateById(customerAccounts);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "客户账号表-通过id删除")
	@ApiOperation(value="客户账号表-通过id删除", notes="客户账号表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		customerAccountsService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "客户账号表-批量删除")
	@ApiOperation(value="客户账号表-批量删除", notes="客户账号表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.customerAccountsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "客户账号表-通过id查询")
	@ApiOperation(value="客户账号表-通过id查询", notes="客户账号表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CustomerAccounts customerAccounts = customerAccountsService.getById(id);
		return Result.ok(customerAccounts);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param customerAccounts
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, CustomerAccounts customerAccounts) {
      return super.exportXls(request, customerAccounts, CustomerAccounts.class, "客户账号表");
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
      return super.importExcel(request, response, CustomerAccounts.class);
  }

}
