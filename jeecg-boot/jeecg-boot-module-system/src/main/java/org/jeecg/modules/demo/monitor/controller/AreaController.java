package org.jeecg.modules.demo.monitor.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.monitor.entity.Area;
import org.jeecg.modules.demo.monitor.mapper.AreaMapper;
import org.jeecg.modules.demo.monitor.service.IAreaService;
import org.jeecg.modules.demo.monitor.vo.AreaDTO;
import org.jeecg.modules.demo.monitor.vo.AreaVo;
import org.jeecg.modules.demo.monitor.vo.DropdownVo;
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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

 /**
 * @Description: 省市表
 * @Author: jeecg-boot
 * @Date:   2020-01-03
 * @Version: V1.0
 */
@Slf4j
@Api(tags="省市表")
@RestController
@RequestMapping("/monitor/area")
public class AreaController extends JeecgController<Area, IAreaService> {
	@Autowired
	private IAreaService areaService;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired 
	private AreaMapper areaMapper;
	/**
	 * 分页列表查询
	 *
	 * @param area
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "省市表-省份分页列表查询")
	@ApiOperation(value="省市表-省份分页列表查询", notes="省市表-省份分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(Area area,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Area> queryWrapper = QueryGenerator.initQueryWrapper(area, req.getParameterMap());
		queryWrapper.eq("num", 0);
		Page<Area> page = new Page<Area>(pageNo, pageSize);
		IPage<Area> pageList = areaService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 分页列表查询
	 *
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "省市表-城市分页列表查询")
	@ApiOperation(value="省市表表-城市分页列表查询", notes="省市表-城市分页列表查询")
	@GetMapping(value = "/queryAreaVoAll")
	public Result<?> queryAreaVoAll(AreaVo area,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AreaVo> queryWrapper = new QueryWrapper<AreaVo>();
		if (area.getNum() != null) {
			queryWrapper.eq("a2.num", area.getNum());
		}
		//一级区域查询
		if (area.getOneAreaName() != null) {
			queryWrapper.eq("a2.num", area.getOneAreaName());
		}
		//城市模糊查询
		if (StrUtil.isNotBlank(area.getTwoAreaName())) {
			queryWrapper.like("a2.Area_name", area.getTwoAreaName());
		}
		Page<AreaVo> page = new Page<AreaVo>(pageNo, pageSize);
		IPage<AreaVo> pageList = areaService.queryAreaVoAll(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param area
	 * @return
	 */
	@AutoLog(value = "省市表-添加")
	@ApiOperation(value="省市表-添加", notes="省市表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Area area) {
		areaService.save(area);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param area
	 * @return
	 */
	@AutoLog(value = "省市表-编辑")
	@ApiOperation(value="省市表-编辑", notes="省市表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Area area) {
		areaService.updateById(area);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "省市表-通过id删除")
	@ApiOperation(value="省市表-通过id删除", notes="省市表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysBaseAPI.addLog("删除省份，id："+id, CommonConstant.LOG_TYPE_2, 3);
		Area area = new Area();
		area.setId(Integer.parseInt(id));
		areaService.updateById(area);
		this.areaService.deleteOne(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "省市表-批量删除")
	@ApiOperation(value="省市表-批量删除", notes="省市表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.areaService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "省市表-通过id查询")
	@ApiOperation(value="省市表-通过id查询", notes="省市表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		Area area = areaService.getById(id);
		return Result.ok(area);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param area
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, Area area) {
      return super.exportXls(request, area, Area.class, "省市表");
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
      return super.importExcel(request, response, Area.class);
  }
  
  //获取所有的省份
  @AutoLog(value = "省市表-获取所有的省份")
	@ApiOperation(value="省市表-获取所有的省份", notes="省市表-获取所有的省份")
  @RequestMapping(value = "/getOneAreaAll",method = RequestMethod.GET)
  public Result<List<Area>> getOneAreaAll(){
	  Result<List<Area>> result = new Result<List<Area>>();
	  LambdaQueryWrapper<Area> queryWrapper = new LambdaQueryWrapper<Area>();
	  queryWrapper.eq(Area::getNum, 0);
	  List<Area> list = areaService.list(queryWrapper);
	  result.setResult(list);
	  result.setSuccess(true);
	  return result;
  }
  
//获取省份下的城市
  @AutoLog(value = "省市表-获取省份下的城市")
 	@ApiOperation(value="省市表-获取省份下的城市", notes="省市表-获取省份下的城市")
  @RequestMapping(value = "/getTwoAreaAll",method = RequestMethod.GET)
  public Result<List<Area>> getTwoAreaAll(@RequestParam(name = "id", required = true) int id){
	  Result<List<Area>> result = new Result<List<Area>>();
	  LambdaQueryWrapper<Area> queryWrapper = new LambdaQueryWrapper<Area>();
	  queryWrapper.eq(Area::getNum, id);
	  List<Area> list = areaService.list(queryWrapper);
	  result.setResult(list);
	  result.setSuccess(true);
	  return result;
  }
  
  //可以分配管理
  @RequestMapping(value = "/queryAllTree",method = {RequestMethod.GET, RequestMethod.POST})
 	public Result<List<Map<String, Object>>> queryAllTree(){
 	  Result<List<Map<String, Object>>> result = new Result<List<Map<String,Object>>>();
 	  List<Map<String, Object>> list = areaService.findOrgUserTree(0);
 	  System.out.println("---------------list------------"+list);
 	  if (list != null && list.size() > 0) {
 		result.setResult(list);
 		result.setSuccess(true);
 	  }else {
 		result.error500("查询数据失败");
 	  }
 		return result;
 	}


	 /**
	  * 根据用户id和客户id保存Distribution
	  * @param areaDTO
	  * @return
	  */
	 @PostMapping("updateCustomersArea")
	 public Result<?> updateCustomersArea(@RequestBody AreaDTO areaDTO) {

		 Boolean b = areaService.updateCustomersArea(areaDTO);

		 if (b) {
			 return Result.ok("保存成功");
		 } else {
			 return Result.error("保存失败");
		 }
	 }



	 /**
	  *  根据用户id查询有哪些客户
	  * @param uid
	  * @return
	  */
	 @GetMapping("findCustomerByUid")
	 public Result<?> findCustomerByUid(@RequestParam("uid") String uid) {
		 Result<List<String>> result = new Result<>();
		 List<String> list = new ArrayList<String>();
		 if (uid != null) {
			 List<Long> listLong = areaService.findCustomerByUid(uid);
			 if(listLong != null) {
				 for(Long id : listLong) {
					 list.add(String.valueOf(id));
				 }
			 }
		 }
		 result.setResult(list);
		 return result;
	 }


	 /**
	  * 查询所有的区域id
	  * @return
	  */
	 @GetMapping("findAllAid")
	 public Result<?> findAllAid() {
		 Result<List<Integer>> result = new Result<>();
		 List<Integer> list = areaService.findAllAid();
		 result.setResult(list);
		 return result;
	 }
	 //根据省份查询省份信息
	  @RequestMapping(value = "/querAreaByNum", method = RequestMethod.GET)
		public Result<Area> querAreaByNum(@RequestParam(name = "num",required = true)Integer num){
			Result<Area> result = new Result<Area>();
			Area area = areaMapper.querAreaByNum(num);
			result.setResult(area);
			result.setSuccess(true);
			return result;
		}




	 //根据用户id查询省份信息\
	 @AutoLog(value = "下拉框根据用户id查询省份信息")
	 @ApiOperation(value="下拉框根据用户id查询省份信息", notes="下拉框根据用户id查询省份信息")
	 @RequestMapping(value = "/querOneAreaByUid", method = RequestMethod.GET)
	 public Result<List<DropdownVo>> querOneAreaByUid(@RequestParam(name = "uid",required = true)String uid){
		 Result<List<DropdownVo>> result = new Result<List<DropdownVo>>();
		 List<DropdownVo> area = areaMapper.querOneAreaByUid(uid);
		 System.out.println(area);
		 result.setResult(area);
		 result.setSuccess(true);
		 return result;
	 }


	 //根据用户id查询城市信息
	 @AutoLog(value = "下拉框根据用户id查询城市信息")
	 @ApiOperation(value="下拉框根据用户id查询城市信息", notes="下拉框根据用户id查询城市信息")
	 @RequestMapping(value = "/querTowAreaByUid", method = RequestMethod.GET)
	 public  Result<List<DropdownVo>>  querTowAreaByUid(@RequestParam(name = "provinceid",required = true)Integer provinceid,
														@RequestParam(name = "uid",required = true)String uid){
		 Result<List<DropdownVo>> result = new Result<List<DropdownVo>>();
		 List<DropdownVo> area = areaMapper.querTowAreaByUid(provinceid,uid);
		 result.setResult(area);
		 result.setSuccess(true);
		 return result;
	 }



	 //根据用户id查询客户信息
	 @AutoLog(value = "下拉框根据用户id查询客户信息")
	 @ApiOperation(value="下拉框根据用户id查询客户信息", notes="下拉框根据用户id查询客户信息")
	 @RequestMapping(value = "/querCustomerByUid", method = RequestMethod.GET)
	 public Result<Object>  querCustomerByUid(
			 @RequestParam(name = "provinceid", defaultValue= "0")Integer provinceid,
			 @RequestParam(name = "cityid", defaultValue= "0")Integer cityid,
			 @RequestParam(name = "uid",required = true)String uid){

		 Result<Object> result = new Result<Object>();
		 List<DropdownVo> area = null;
		 if("0".equals(provinceid.toString())) {
			 area = areaMapper.querOneAreaByUid(uid);
			 System.out.println(area);
			 return Result.ok(area);
		 }
		 if("0".equals(cityid.toString())) {
			 area = areaMapper.querTowAreaByUid(provinceid,uid);
			 System.out.println(area);
			 return Result.ok(area);
		 }

		 System.out.println("-------------------"+provinceid+cityid+uid);
		 area = areaMapper.querCustomerByUid(provinceid,cityid,uid);
		 System.out.println("-------------------"+area);
		 result.setResult(area);
		 result.setSuccess(true);
		 return result;
	 }






}
