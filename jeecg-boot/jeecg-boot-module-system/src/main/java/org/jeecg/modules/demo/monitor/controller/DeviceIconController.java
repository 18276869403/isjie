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
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.monitor.entity.DeviceIcon;
import org.jeecg.modules.demo.monitor.service.IDeviceIconService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 设备图标表
 * @Author: jeecg-boot
 * @Date:   2019-12-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags="设备图标表")
@RestController
@RequestMapping("/monitor/deviceIcon")
public class DeviceIconController extends JeecgController<DeviceIcon, IDeviceIconService> {
	@Autowired
	private IDeviceIconService deviceIconService;
	
	/**
	 * 分页列表查询
	 *
	 * @param deviceIcon
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	// @AutoLog(value = "设备图标表-分页列表查询")
	@ApiOperation(value="设备图标表-分页列表查询", notes="设备图标表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(DeviceIcon deviceIcon,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DeviceIcon> queryWrapper = QueryGenerator.initQueryWrapper(deviceIcon, req.getParameterMap());
		Page<DeviceIcon> page = new Page<DeviceIcon>(pageNo, pageSize);
		IPage<DeviceIcon> pageList = deviceIconService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param deviceIcon
	 * @return
	 */
	// @AutoLog(value = "设备图标表-添加")
	@ApiOperation(value="设备图标表-添加", notes="设备图标表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DeviceIcon deviceIcon) {
		deviceIconService.save(deviceIcon);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param deviceIcon
	 * @return
	 */
	// @AutoLog(value = "设备图标表-编辑")
	@ApiOperation(value="设备图标表-编辑", notes="设备图标表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DeviceIcon deviceIcon) {
		deviceIconService.updateById(deviceIcon);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	// @AutoLog(value = "设备图标表-通过id删除")
	@ApiOperation(value="设备图标表-通过id删除", notes="设备图标表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		deviceIconService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	// @AutoLog(value = "设备图标表-批量删除")
	@ApiOperation(value="设备图标表-批量删除", notes="设备图标表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.deviceIconService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	// @AutoLog(value = "设备图标表-通过id查询")
	@ApiOperation(value="设备图标表-通过id查询", notes="设备图标表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		DeviceIcon deviceIcon = deviceIconService.getById(id);
		return Result.ok(deviceIcon);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param deviceIcon
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, DeviceIcon deviceIcon) {
      return super.exportXls(request, deviceIcon, DeviceIcon.class, "设备图标表");
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
      return super.importExcel(request, response, DeviceIcon.class);
  }

}
