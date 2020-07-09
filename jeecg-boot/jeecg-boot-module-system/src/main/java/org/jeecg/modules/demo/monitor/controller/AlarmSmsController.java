package org.jeecg.modules.demo.monitor.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.monitor.entity.AlarmSms;
import org.jeecg.modules.demo.monitor.service.IAlarmSmsService;
import org.jeecg.modules.demo.monitor.vo.AlarmSmsVo;

import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
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
 * @Description: 报警短信表
 * @Author: jeecg-boot
 * @Date:   2020-01-13
 * @Version: V1.0
 */
@Slf4j
@Api(tags="报警短信表")
@RestController
@RequestMapping("/monitor/alarmSms")
public class AlarmSmsController extends JeecgController<AlarmSms, IAlarmSmsService> {
	@Autowired
	private IAlarmSmsService alarmSmsService;


	 // @AutoLog(value = "报警短信表-清空")
	 @ApiOperation(value = "报警短信表-清空", notes = "报警短信表-清空")
	 @GetMapping(value = "/deleteAll")
	 public Result<?> deleteAll() {
		 alarmSmsService.deleteAll();
		 return Result.ok("已清空");
	 }
	
	/**
	 * 报警短信分页列表查询
	 *
	 * @param alarmSms
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	// @AutoLog(value = "报警短信表-分页列表查询")
	@ApiOperation(value="报警短信表-分页列表查询", notes="报警短信表-分页列表查询")
	@GetMapping(value = "/querySmsVo")
	public Result<?> queryPageList(AlarmSmsVo alarmSms,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AlarmSmsVo> queryWrapper = new QueryWrapper<AlarmSmsVo>();
		queryWrapper.orderByDesc("a.alarm_date");
		//根据客户id查询报警短信记录
		if (alarmSms.getCustomerId()!=null) {
			queryWrapper.eq("d.Customer_id", alarmSms.getCustomerId());
		}
		// 一二级区域查询
		if (alarmSms.getOneAreaName() != null) {
			queryWrapper.eq("c.One_area_id", alarmSms.getOneAreaName());
		}
		if (alarmSms.getTwoAreaName() != null) {
			queryWrapper.eq("c.Two_area_id", alarmSms.getTwoAreaName());
		}
		
		// 客户名称查询
		if (alarmSms.getName() != null) {
			queryWrapper.eq("d.Customer_id", alarmSms.getName());
		}
		
		// 设备IMEI
		if (StrUtil.isNotBlank(alarmSms.getAlarmDeviceImei())) {
			queryWrapper.like("a.Alarm_deviceImei", alarmSms.getAlarmDeviceImei());
		}
		// 设备类型
		if (StrUtil.isNotBlank(alarmSms.getEquipType())) {
			queryWrapper.like("a.equip_type", alarmSms.getEquipType());
		}
		// 是否发送短信
		if (StrUtil.isNotBlank(alarmSms.getSmsType())) {
			queryWrapper.like("a.sms_type", alarmSms.getSmsType());
		}
		super.appendDeviceAreaAuth(req, queryWrapper);
		Page<AlarmSmsVo> page = new Page<AlarmSmsVo>(pageNo, pageSize);
		IPage<AlarmSmsVo> pageList = alarmSmsService.selectAlarmSms(page, queryWrapper);
		return Result.ok(pageList);
	} 
	
	
	/**
	 * 分页列表查询
	 *
	 * @param alarmSms
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	// @AutoLog(value = "报警短信表-分页列表查询")
	@ApiOperation(value="报警短信表-分页列表查询", notes="报警短信表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(AlarmSms alarmSms,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AlarmSms> queryWrapper = QueryGenerator.initQueryWrapper(alarmSms, req.getParameterMap());
		queryWrapper.orderByDesc("alarm_date");
		Page<AlarmSms> page = new Page<AlarmSms>(pageNo, pageSize);
		IPage<AlarmSms> pageList = alarmSmsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param alarmSms
	 * @return
	 */
	// @AutoLog(value = "报警短信表-添加")
	@ApiOperation(value="报警短信表-添加", notes="报警短信表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody AlarmSms alarmSms) {
		alarmSmsService.save(alarmSms);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param alarmSms
	 * @return
	 */
	// @AutoLog(value = "报警短信表-编辑")
	@ApiOperation(value="报警短信表-编辑", notes="报警短信表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody AlarmSms alarmSms) {
		alarmSmsService.updateById(alarmSms);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	// @AutoLog(value = "报警短信表-通过id删除")
	@ApiOperation(value="报警短信表-通过id删除", notes="报警短信表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		alarmSmsService.removeById(id.split("-")[0]);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	// @AutoLog(value = "报警短信表-批量删除")
	@ApiOperation(value="报警短信表-批量删除", notes="报警短信表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> ss = Arrays.asList(ids.split(","));
		for (String string : ss) {
			alarmSmsService.removeById(string.split("-")[0]);
		}
//this.alarmSmsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	

	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	// @AutoLog(value = "报警短信表-通过id查询")
	@ApiOperation(value="报警短信表-通过id查询", notes="报警短信表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		AlarmSms alarmSms = alarmSmsService.getById(id);
		return Result.ok(alarmSms);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param alarmSms
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, AlarmSms alarmSms) {
      return super.exportXls(request, alarmSms, AlarmSms.class, "报警短信表");
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
      return super.importExcel(request, response, AlarmSms.class);
  }

}
