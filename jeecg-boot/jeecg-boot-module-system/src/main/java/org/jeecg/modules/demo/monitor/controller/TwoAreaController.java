package org.jeecg.modules.demo.monitor.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.monitor.entity.OneArea;
import org.jeecg.modules.demo.monitor.entity.TwoArea;
import org.jeecg.modules.demo.monitor.service.IOneAreaService;
import org.jeecg.modules.demo.monitor.service.ITwoAreaService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;

 /**
 * @Description: 市级区域表
 * @Author: jeecg-boot
 * @Date:   2019-12-24
 * @Version: V1.0
 */
@RestController
@RequestMapping("/monitor/twoArea")
@Slf4j
public class TwoAreaController extends JeecgController<TwoArea, ITwoAreaService> {
	 @Autowired
	 private ITwoAreaService twoAreaService;
	 @Autowired
	 private IOneAreaService oneAreaService;
	
	/**
	 * 分页列表查询
	 *
	 * @param twoArea
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	// @AutoLog(value = "市级区域-分页列表")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TwoArea twoArea,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TwoArea> queryWrapper = QueryGenerator.initQueryWrapper(twoArea, req.getParameterMap());
		Page<TwoArea> page = new Page<TwoArea>(pageNo, pageSize);
		IPage<TwoArea> pageList = twoAreaService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param twoArea
	 * @return
	 */
	// @AutoLog(value = "市级区域-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TwoArea twoArea) {

        if(twoArea.getDelFlag()==null){
            twoArea.setDelFlag(0);
        }
		twoAreaService.save(twoArea);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param twoArea
	 * @return
	 */
	// @AutoLog(value = "市级区域-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TwoArea twoArea) {
		twoAreaService.updateById(twoArea);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	// @AutoLog(value = "市级区域-通过id删除")
	@ApiOperation(value="市级区域-通过id删除", notes="市级区域-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		TwoArea twoArea=new TwoArea();
		twoArea.setId(Integer.parseInt(id));
		this.twoAreaService.updateById(twoArea);
		this.twoAreaService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.twoAreaService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TwoArea twoArea = twoAreaService.getById(id);
		if(twoArea==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(twoArea);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param twoArea
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TwoArea twoArea) {
        return super.exportXls(request, twoArea, TwoArea.class, "市级区域表");
    }

	 /**
	  * 获取指定的市级区域分页列表查询
	  * @param twoArea
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 // @AutoLog(value = "获取指定的市级区域-分页列表查询")
	 @ApiOperation(value = "指定市级区域-分页列表查询", notes = "获取指定的市级区域-分页列表查询")
	 @GetMapping(value = "/appointlist")
	 public Result<?> queryPageAppointList(TwoArea twoArea,
										   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
										   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
										   @RequestParam(name = "oneid", required = true) String oneid, HttpServletRequest req) {
		if(oneid!=""){
			twoArea.setOneAreaId(Integer.parseInt(oneid));
			QueryWrapper<TwoArea> queryWrapper = QueryGenerator.initQueryWrapper(twoArea, req.getParameterMap());
			Page<TwoArea> page = new Page<TwoArea>(pageNo, pageSize);
			IPage<TwoArea> pageList = twoAreaService.page(page, queryWrapper);
			return Result.ok(pageList);
		}else{
			QueryWrapper<TwoArea> queryWrapper = QueryGenerator.initQueryWrapper(twoArea, req.getParameterMap());
			Page<TwoArea> page = new Page<TwoArea>(pageNo, pageSize);
			IPage<TwoArea> pageList = twoAreaService.page(page, queryWrapper);
			return Result.ok(pageList);
		}
	 }

	 /**
	  * 禁用&恢复市级区域
	  * @param jsonObject
	  * @return
	  */
	 @RequestMapping(value = "/twoStatus", method = RequestMethod.PUT)
	 public Result<TwoArea> twoStatus(@RequestBody JSONObject jsonObject) {
		 String oneAreaId = jsonObject.getString("oneAreaId");
		 OneArea oneArea = oneAreaService.getById(oneAreaId);
		 Result<TwoArea> result = new Result<TwoArea>();
		 if (oneArea.getDelFlag()==1) {
			 result.success("省份被禁用!");
		 }else {
			 try {
				 String ids = jsonObject.getString("ids");
				 String delFlag = jsonObject.getString("delFlag");
				 String[] arr = ids.split(",");
				 for (String id : arr) {
					 TwoArea twoArea = new TwoArea();
					 twoArea.setDelFlag(Integer.parseInt(delFlag));
					 if(oConvertUtils.isNotEmpty(id)) {
						 this.twoAreaService.update(twoArea,
								 new UpdateWrapper<TwoArea>().lambda().eq(TwoArea::getId,id));
					 }
				 }
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 result.error500("操作失败"+e.getMessage());
			 }
			 result.success("操作成功!");
		 }
		 return result;

	 }

	 /**
	  *需求列表根据省级区域 获取指定的市级区域
	  * @param
	  * @return
	  */
	 @RequestMapping(value = "/needGetTwoArea", method = RequestMethod.GET)
	 public Result<List<TwoArea>> oneallStatus(@RequestParam(name = "id", required = true) String id) {
		 Result<List<TwoArea>> result = new Result<>();
		 LambdaQueryWrapper<TwoArea> query = new LambdaQueryWrapper<TwoArea>();
		 query.eq(TwoArea::getOneAreaId, id);
		 List<TwoArea> list = twoAreaService.list(query);
		 if(list==null||list.size()<=0) {
			 result.error500("未找到区域");
		 }else {
			 result.setResult(list);
			 result.setSuccess(true);
		 }
		 return result;
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
        return super.importExcel(request, response, TwoArea.class);
    }
    
    


	// 获取一级区域下的二级区域
	@RequestMapping(value = "/getTwoAreaAll", method = RequestMethod.GET)
	public Result<List<TwoArea>> getTwoAreaAll(@RequestParam(name = "id", required = true) int id) {
		Result<List<TwoArea>> result = new Result<List<TwoArea>>();
		LambdaQueryWrapper<TwoArea> queryWrapper = new LambdaQueryWrapper<TwoArea>();
		queryWrapper.eq(TwoArea::getOneAreaId, id);
		List<TwoArea> list = twoAreaService.list(queryWrapper);
		result.setResult(list);
		result.setSuccess(true);
		return result;
	}
	
}
