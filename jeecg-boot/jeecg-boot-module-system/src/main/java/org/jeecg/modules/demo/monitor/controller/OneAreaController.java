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
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.monitor.entity.OneArea;
import org.jeecg.modules.demo.monitor.entity.TwoArea;
import org.jeecg.modules.demo.monitor.service.IOneAreaService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.monitor.service.ITwoAreaService;
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
 * @Description: 省级区域表
 * @Author: jeecg-boot
 * @Date:   2019-12-24
 * @Version: V1.0
 */
@RestController
@RequestMapping("/monitor/oneArea")
@Slf4j
public class OneAreaController extends JeecgController<OneArea, IOneAreaService> {


	@Autowired
	private IOneAreaService oneAreaService;
	@Autowired
	private ITwoAreaService twoAreaService;

	@Autowired
	private ISysBaseAPI sysBaseAPI;

	/**
	 * 分页列表查询
	 *
	 * @param oneArea
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OneArea oneArea,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OneArea> queryWrapper = QueryGenerator.initQueryWrapper(oneArea, req.getParameterMap());
		Page<OneArea> page = new Page<OneArea>(pageNo, pageSize);
		IPage<OneArea> pageList = oneAreaService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 *   添加
	 *
	 * @param oneArea
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OneArea oneArea) {
		if(oneArea.getDelFlag()==null){
			oneArea.setDelFlag(0);
		}
		oneAreaService.save(oneArea);
		return Result.ok("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param oneArea
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OneArea oneArea) {
		oneAreaService.updateById(oneArea);
		return Result.ok("编辑成功!");
	}

	 /**
	  * 获取全部省级区域
	  * @param
	  * @return
	  */
	 @RequestMapping(value = "/getoneall", method = RequestMethod.GET)
	 public Result<List<OneArea>> oneall() {
		 Result<List<OneArea>> result = new Result<>();
		 List<OneArea> list = oneAreaService.list();
		 if(list==null||list.size()<=0) {
			 result.error500("未找到区域");
		 }else {
			 result.setResult(list);
			 result.setSuccess(true);
		 }
		 return result;
	 }

	 /**
	  * 获取未被禁用的一级分类
	  * @param
	  * @return
	  */
	 @RequestMapping(value = "/GetoneallStatus", method = RequestMethod.GET)
	 public Result<List<OneArea>> NeedGetoneallStatus() {
		 Result<List<OneArea>> result = new Result<>();
		 LambdaQueryWrapper<OneArea> query = new LambdaQueryWrapper<OneArea>();
		 query.eq(OneArea::getDelFlag, 0);
		 List<OneArea> list = oneAreaService.list(query);
		 if(list==null||list.size()<=0) {
			 result.error500("未找到区域");
		 }else {
			 result.setResult(list);
			 result.setSuccess(true);
		 }
		 return result;
	 }
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.oneAreaService.removeByIds(Arrays.asList(ids.split(",")));
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
		OneArea oneArea = oneAreaService.getById(id);
		if(oneArea==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(oneArea);
	}
	 	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "省级区域-通过id删除")
	@ApiOperation(value="省级区域-通过id删除", notes="省级区域-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysBaseAPI.addLog("删除省份，id： " +id , CommonConstant.LOG_TYPE_2, 3);
		OneArea oneArea=new OneArea();
		oneArea.setId(Integer.parseInt(id));
		//oneArea.setClassSort(0);
		oneAreaService.updateById(oneArea);
		this.oneAreaService.deleteOne(id);
		return Result.ok("删除省份成功");
	}

    /**
    * 导出excel
    *
    * @param request
    * @param oneArea
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, OneArea oneArea) {
        return super.exportXls(request, oneArea, OneArea.class, "省级区域表");
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
        return super.importExcel(request, response, OneArea.class);
    }

	 /**
	  	 * 禁用&恢复省级区域同时禁用&恢复市级区域
	  	 * @param jsonObject
	  	 * @return
	  	 */
	@RequestMapping(value = "/oneStatus", method = RequestMethod.PUT)
	public Result<OneArea> frozenBatch(@RequestBody JSONObject jsonObject) {
		Result<OneArea> result = new Result<OneArea>();
//		Result<TwoClass> results = new Result<TwoClass>();
		try {
			String ids = jsonObject.getString("ids");
			String delFlag = jsonObject.getString("delFlag");
			String[] arr = ids.split(",");
			for (String id : arr) {
				if(oConvertUtils.isNotEmpty(id)) {
					OneArea oneArea = new OneArea();
					oneArea.setDelFlag(Integer.parseInt(delFlag));
					this.oneAreaService.update(oneArea,
							new UpdateWrapper<OneArea>().lambda().eq(OneArea::getId,id));
					for (String tid : arr) {
						if(oConvertUtils.isNotEmpty(tid)) {
							TwoArea twoArea = new TwoArea();
							twoArea.setDelFlag(Integer.parseInt(delFlag));
							this.twoAreaService.update(twoArea,
									new UpdateWrapper<TwoArea>().lambda().eq(TwoArea::getOneAreaId,tid));
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败"+e.getMessage());
		}
		result.success("操作成功!");
		return result;

	}
	
	


	// 获取所有的一级区域
	@RequestMapping(value = "/getOneAreaAll", method = RequestMethod.GET)
	public Result<List<OneArea>> getOneAreaAll() {
		Result<List<OneArea>> result = new Result<List<OneArea>>();
		LambdaQueryWrapper<OneArea> queryWrapper = new LambdaQueryWrapper<OneArea>();
		List<OneArea> list = oneAreaService.list(queryWrapper);
		result.setResult(list);
		result.setSuccess(true);
		return result;
	}

}