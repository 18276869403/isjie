package org.jeecg.modules.demo.monitor.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.monitor.entity.FloorPlan;
import org.jeecg.modules.demo.monitor.mapper.DeviceMapper;
import org.jeecg.modules.demo.monitor.service.IDeviceService;
import org.jeecg.modules.demo.monitor.service.IFloorPlanService;
import org.jeecg.modules.demo.monitor.vo.DeviceInfoDTO;
import org.jeecg.modules.demo.monitor.vo.FloorPlanDTO;
import org.jeecg.modules.demo.monitor.vo.FlorVo;
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
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

 /**
 * @Description: 楼层图表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags="楼层图表")
@RestController
@RequestMapping("/monitor/floorPlan")
public class FloorPlanController extends JeecgController<FloorPlan, IFloorPlanService> {
	@Autowired
	private IFloorPlanService floorPlanService;
	@Autowired
	private IDeviceService deviceService;
	@Autowired
	private DeviceMapper deviceMapper;
	 @Autowired
	 private ISysBaseAPI sysBaseAPI;
	
	/**
	 * 分页列表查询
	 *
	 * @param floorPlan
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	// @AutoLog(value = "楼层图表-分页列表查询")
	@ApiOperation(value="楼层图表-分页列表查询", notes="楼层图表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(FloorPlan floorPlan,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FloorPlan> queryWrapper = QueryGenerator.initQueryWrapper(floorPlan, req.getParameterMap());
		Page<FloorPlan> page = new Page<FloorPlan>(pageNo, pageSize);
		IPage<FloorPlan> pageList = floorPlanService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param floorPlan
	 * @return
	 */
	// @AutoLog(value = "楼层图表-添加")
	@ApiOperation(value="楼层图表-添加", notes="楼层图表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody FloorPlan floorPlan) {
		floorPlanService.save(floorPlan);
		try {
			floorPlanService.saveFloorAndDevice(floorPlan);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
		return Result.ok("添加成功！");
	}


	/**
	 * 编辑
	 * @param floorPlan
	 * @return
	 */
	// @AutoLog(value = "楼层图表-编辑")
	@ApiOperation(value="楼层图表-编辑", notes="楼层图表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody FloorPlan floorPlan) {
		floorPlanService.updateById(floorPlan);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	// @AutoLog(value = "楼层图表-通过id删除")
	@ApiOperation(value="楼层图表-通过id删除", notes="楼层图表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		floorPlanService.removeById(id);
		floorPlanService.deletefloordevice(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	// @AutoLog(value = "楼层图表-批量删除")
	@ApiOperation(value="楼层图表-批量删除", notes="楼层图表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.floorPlanService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	// @AutoLog(value = "楼层图表-通过id查询")
	@ApiOperation(value="楼层图表-通过id查询", notes="楼层图表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		FloorPlan floorPlan = floorPlanService.getById(id);
		return Result.ok(floorPlan);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param floorPlan
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, FloorPlan floorPlan) {
      return super.exportXls(request, floorPlan, FloorPlan.class, "楼层图表");
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
      return super.importExcel(request, response, FloorPlan.class);
  }

  
  //楼层平面图 2019-12-26 第一版
	// @AutoLog(value = "楼层平面图-分页列表查询")
	@ApiOperation(value = "楼层平面图-分页列表查询", notes = "楼层平面图-分页列表查询")
	@GetMapping(value = "/Florlist")
	public Result<?> queryPageList1(FlorVo florVo,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<FlorVo> queryWrapper = new QueryWrapper<FlorVo>();
//		String header = req.getHeader("X-Access-Token");
//		String username = JwtUtil.getUsername(header);
//		LoginUser sysUser = sysBaseAPI.getUserByName(username);
//		queryWrapper.eq("dis.User_Id",sysUser.getId());
		super.appendCustomerAuth(req, queryWrapper);
		if (florVo.getCustomerId() != null) {
			queryWrapper.eq("flor.Customer_id", florVo.getCustomerId());
		}
		if (florVo.getOneAreaName() != null) {
			queryWrapper.eq("c.One_area_id", florVo.getOneAreaName());
		}
		if (florVo.getTwoAreaName() != null) {
			queryWrapper.eq("c.Two_area_id", florVo.getTwoAreaName());
		}
		if (StrUtil.isNotBlank(florVo.getName())) {
			queryWrapper.like("c.name", florVo.getName());
		}
		if(StrUtil.isNotBlank(florVo.getFloorName())) {
			queryWrapper.like("flor.Floor_name", florVo.getFloorName());
		}
		Page<FlorVo> page = new Page<FlorVo>(pageNo, pageSize);
		page.addOrder(OrderItem.asc("backUp1"));
		IPage<FlorVo> pageList = floorPlanService.custoMpage(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	//楼层平面图 2019-12-26 第一版
		// @AutoLog(value = "前端-实时数据分页列表查询")
		@ApiOperation(value = "前端-实时数据分页列表查询", notes = "前端-实时数据分页列表查询")
		@GetMapping(value = "/webSelectFlor")
		public Result<?> webSelectFlor(FlorVo florVo,
				@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
				@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
			QueryWrapper<FlorVo> queryWrapper = new QueryWrapper<FlorVo>();
			
			Page<FlorVo> page = new Page<FlorVo>(pageNo, pageSize);
			IPage<FlorVo> pageList = floorPlanService.webselectFlor(page, queryWrapper);
			return Result.ok(pageList);
		}
		/**
		   * 获取客户的楼层名
		   *
		   * @param request
		   * @param response
		   * @return
		   */
		// @AutoLog(value = "楼层平面图-获取客户的楼层名")
		@ApiOperation(value = "楼层平面图-获取客户的楼层名", notes = "楼层平面图-获取客户的楼层名")
		@GetMapping(value = "/queryFloorAll")
		public Result<List<FloorPlan>> queryFloorAll(@RequestParam(name = "id", required = true) int id){
			Result<List<FloorPlan>> result = new Result<List<FloorPlan>>();
			LambdaQueryWrapper<FloorPlan> queryWrapper = new LambdaQueryWrapper<FloorPlan>();
			queryWrapper.eq(FloorPlan::getCustomerId, id);
			List<FloorPlan> list = floorPlanService.list(queryWrapper);
			 if(list==null||list.size()<=0) {
				 result.error500("未找到楼层平面图信息");
			 }else {
				 result.setResult(list);
				 result.setSuccess(true);
			 }
			 return result;
		}

	 /**
	  * 根据用户id查询所有的楼层信息
	  * @param cid
	  * @return
	  */
	 @GetMapping("queryFloorNameAndIdByCid")
	 @ApiOperation(value = "根据用户id查询所有的楼层信息", notes = "根据用户id查询所有的楼层信息")
	 public Result<?> queryFloorNameAndIdByCid(HttpServletRequest request, 
			 @RequestParam("cid") Integer cid) {
		 List<FloorPlanDTO> floorPlanDTOS= floorPlanService.queryFloorNameAndIdByCid(cid,
				 super.getLoginUser(request).getId());

		 return Result.ok(floorPlanDTOS);

	 }

	 /**
	  * 根据楼层id查询该楼层详细信息
	  * @param fid
	  * @return
	  */
	 @GetMapping("queryFloorDeviceByFid")
	 @ApiOperation(value = "根据楼层id查询该楼层详细信息", notes = "根据楼层id查询该楼层详细信息")
	 public Result<?> queryFloorDeviceByFid(HttpServletRequest req, @RequestParam("fid") Integer fid) {
		 HashMap<String, Object> map = new HashMap<>();
//		 Result<Map<String, Object>> result = new Result<>();
		 FloorPlan byId = floorPlanService.getById(fid);
		 if(byId != null) {
			 String picurl = byId.getPlanPic();
			 List<DeviceInfoDTO> deviceInfoDTOS = floorPlanService.queryFloorDeviceByFid(fid,
					 super.getLoginUser(req).getId());
			 map.put("picurl", picurl);
			 map.put("deviceInfoDTOS", deviceInfoDTOS);
		 }

		 return Result.ok(map);


	 }
	 /**
	  * 查询所有的区域，厂房F
	  * @param
	  * @return
	  */
	 @GetMapping("queryFloorBack")
	 @ApiOperation(value = "根据id查询所有的区域，厂房", notes = "根据id查询所有的区域，厂房")
	 public Result<?> queryFloorBack() {
		 QueryWrapper queryWrapper = new QueryWrapper();
		 queryWrapper.select("DISTINCT BackUp2,BackUp3");

		 List list = floorPlanService.list(queryWrapper);
                    return Result.ok(list);
	 }
		
}

