package org.jeecg.modules.demo.monitor.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.monitor.entity.DeviceArea;
import org.jeecg.modules.demo.monitor.service.IDeviceAreaService;
import org.jeecg.modules.demo.monitor.vo.DeviceAreaVO;
import org.jeecg.modules.demo.monitor.vo.DeviceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 设备区域
 * @Author: jeecg-boot
 * @Date: 2019-12-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "设备区域")
@RestController
@RequestMapping("/monitor/deviceArea")
public class DeviceAreaController extends JeecgController<DeviceArea, IDeviceAreaService> {
	@Autowired
	private IDeviceAreaService deviceAreaService;
	@Autowired
	private ISysBaseAPI sysBaseAPI;

	/**
	 * 分页列表查询
	 *
	 * @param device
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "设备区域-分页列表查询")
	@ApiOperation(value = "设备区域-分页列表查询", notes = "设备区域-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(DeviceAreaVO device, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<DeviceAreaVO> queryWrapper = new QueryWrapper<DeviceAreaVO>();//QueryGenerator.initQueryWrapper(device, req.getParameterMap());
		String tmp = device.getAreaName();
		if(StringUtils.isNotEmpty(tmp)) {
			queryWrapper.like("area_name", "%" + tmp + "%");
		}
		tmp = device.getCustomerName();
		if(StringUtils.isNotEmpty(tmp)) {
			queryWrapper.like("b.`name`", "%" + tmp + "%");
		}
		Page<DeviceAreaVO> page = new Page<DeviceAreaVO>(pageNo, pageSize);
		IPage<DeviceAreaVO> pageList = deviceAreaService.custoMpage(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param device
	 * @return
	 */
	//@AutoLog(value = "设备区域-添加")
	@ApiOperation(value = "设备区域-添加", notes = "设备区域-添加")
	@PostMapping(value = "/add")
	public Result<?> add(HttpServletRequest req,
			@RequestBody DeviceArea deviceArea) {
		String header = req.getHeader("X-Access-Token");
//		ShiroRealm shiroRealm = new ShiroRealm();
		String username = JwtUtil.getUsername(header);
		LoginUser sysUser = sysBaseAPI.getUserByName(username);
		deviceArea.setCreateBy(sysUser.getId());
		deviceArea.setCreateTime(new Date());
		deviceAreaService.save(deviceArea);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param device
	 * @return
	 */
	//@AutoLog(value = "设备区域-编辑")
	@ApiOperation(value = "设备区域-编辑", notes = "设备区域-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DeviceArea deviceArea) {
		deviceArea.setUpdateTime(new Date());
		deviceAreaService.updateById(deviceArea);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "设备区域-通过id删除")
	@ApiOperation(value = "设备区域-通过id删除", notes = "设备区域-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		deviceAreaService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	//@AutoLog(value = "设备区域-批量删除")
	@ApiOperation(value = "设备区域-批量删除", notes = "设备区域-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.deviceAreaService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "设备区域-通过id查询")
	@ApiOperation(value = "设备区域-通过id查询", notes = "设备区域-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		DeviceArea deviceArea = deviceAreaService.getById(id);
		return Result.ok(deviceArea);
	}



	/**
	 * 导出excel
	 *
	 * @param request
	 * @param device
	 */
	@RequestMapping(value = "/exportXls")
//	@ApiOperation(value = "设备-导出excel", notes = "设备-导出excel")
	public ModelAndView exportXls(HttpServletRequest request, DeviceVo device) {
		return null;
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
		return null;
	}
	@ApiOperation(value = "按客户查设备区域")
	@GetMapping(value = "/queryByCustomerId")
	@ResponseBody
	public Result<?> queryByCustomerId(DeviceAreaVO device, 
			@RequestParam(name = "customerId") Integer customerId, 
			HttpServletRequest req) {
		QueryWrapper<DeviceArea> queryWrapper = new QueryWrapper<DeviceArea>();
		queryWrapper.eq("customer_id", customerId);
		List<DeviceArea> daList = deviceAreaService.list(queryWrapper);
		JSONArray data = new JSONArray();
		if(daList != null && daList.size() > 0) {
			for(DeviceArea da : daList) {
				JSONObject jo = new JSONObject();
				jo.put("id", String.valueOf(da.getId()));
				jo.put("areaName", da.getAreaName());
				data.add(jo);
			}
		}
		return Result.ok(data);
	}





	// 获取所有的区域（下拉框）
	@ApiOperation(value = "获取所有的区域（下拉框）")
	@RequestMapping(value = "/getAreaNameAll", method = RequestMethod.GET)
	public Result<List<DeviceArea>> getAreaNameAll(@RequestParam(name = "id", required = true) int id) {
		Result<List<DeviceArea>> result = new Result<List<DeviceArea>>();
		LambdaQueryWrapper<DeviceArea> queryWrapper = new LambdaQueryWrapper<DeviceArea>();
		List<DeviceArea> list = deviceAreaService.list(queryWrapper);
		queryWrapper.eq(DeviceArea::getCustomerId, id);
		result.setResult(list);
		result.setSuccess(true);
		return result;
	}
}
