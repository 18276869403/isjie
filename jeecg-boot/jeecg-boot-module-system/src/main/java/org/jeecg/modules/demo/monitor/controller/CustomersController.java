package org.jeecg.modules.demo.monitor.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.monitor.entity.Area;
import org.jeecg.modules.demo.monitor.entity.Customers;
import org.jeecg.modules.demo.monitor.entity.Distribution;
import org.jeecg.modules.demo.monitor.mapper.CustomersMapper;
import org.jeecg.modules.demo.monitor.mapper.DeviceMapper;
import org.jeecg.modules.demo.monitor.service.IAreaService;
import org.jeecg.modules.demo.monitor.service.ICustomersService;
import org.jeecg.modules.demo.monitor.service.IDistributionService;
import org.jeecg.modules.demo.monitor.vo.CustomersVo;
import org.jeecg.modules.demo.monitor.vo.LoadCustomersVo;
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
 * @Description: 客户表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags="客户表")
@RestController
@RequestMapping("/monitor/customers")
public class CustomersController extends JeecgController<Customers, ICustomersService> {
	@Autowired
	private ICustomersService customersService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private CustomersMapper custMapper;
	@Autowired
	private IDistributionService distributionService;
	@Autowired
	private DeviceMapper deviceMapper;
	@Autowired
	private ISysBaseAPI sysBaseAPI;


	/**
	 * 分页列表查询
	 *
	 * @param customers
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "客户表-分页列表查询")
	@ApiOperation(value="客户表-分页列表查询", notes="客户表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(Customers customers,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Customers> queryWrapper = QueryGenerator.initQueryWrapper(customers, req.getParameterMap());
		Page<Customers> page = new Page<Customers>(pageNo, pageSize);
		IPage<Customers> pageList = customersService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 分页客户列表查询
	 *
	 * @param customers
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "客户列表-分页列表查询")
	@ApiOperation(value="客户列表-分页列表查询", notes="客户列表-分页列表查询")
	@GetMapping(value = "/queryCustomersVoAll")
	public Result<?> queryCustomersVoAll(CustomersVo customers,
										 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
										 HttpServletRequest req) {
		QueryWrapper<CustomersVo> queryWrapper = new QueryWrapper<CustomersVo>();

//		String header = req.getHeader("X-Access-Token");
//		String username = JwtUtil.getUsername(header);
//		LoginUser sysUser = sysBaseAPI.getUserByName(username);
//		queryWrapper.eq("dis.User_Id",sysUser.getId());
		super.appendCustomerAuth(req, queryWrapper);
		//一级区域查询
		if (customers.getOneAreaName() != null) {
			queryWrapper.eq("c.One_area_id", customers.getOneAreaName());
		}
		//二级区域
		if (customers.getTwoAreaName() != null) {
			queryWrapper.eq("c.Two_area_id", customers.getTwoAreaName());
		}
		//客户名模糊查询
		if (StrUtil.isNotBlank(customers.getName())) {
			queryWrapper.like("c.name", customers.getName());
		}
		Page<CustomersVo> page = new Page<CustomersVo>(pageNo, pageSize);
		IPage<CustomersVo> pageList = customersService.queryCustomersVoAll(page, queryWrapper);
		return Result.ok(pageList);
	}
	//获取所有的客户
	@AutoLog(value = "客户表-获取所有的客户")
	@ApiOperation(value="客户表-获取所有的客户", notes="省市表-获取所有的客户")
	@RequestMapping(value = "/getCustomersAll", method = RequestMethod.GET)
	public Result<List<Customers>> getCustomersAll(@RequestParam(name = "id", required = true) int id) {
		Result<List<Customers>> result = new Result<List<Customers>>();
		LambdaQueryWrapper<Customers> queryWrapper = new LambdaQueryWrapper<Customers>();
		queryWrapper.eq(Customers::getTwoAreaId, id);
		List<Customers> list = customersService.list(queryWrapper);
//		for(Customers customers: list){
//			if(StrUtil.isNotBlank(customers.getProjectName())){
//				customers.setName(customers.getName()+"-"+customers.getProjectName());
//			}
//		}
		result.setResult(list);
		result.setSuccess(true);
		return result;
	}

	//根据客户id查询客户
	@AutoLog(value = "客户表-根据客户id查询客户")
	@ApiOperation(value="客户表-根据客户id查询客户", notes="省市表-根据客户id查询客户")
	@RequestMapping(value = "/queryAllByCustId", method = RequestMethod.GET)
	public Result<CustomersVo> queryAllByCustId(@RequestParam(name = "customerId",required = true)int customerId){
		Result<CustomersVo> result = new Result<CustomersVo>();
		CustomersVo customersVo = custMapper.queryAllByCustId(customerId);
		result.setResult(customersVo);
		result.setSuccess(true);
		return result;
	}

	/**
	 * id查询客户列
	 *
	 * @param customers
	 *
	 * @return
	 */
	@AutoLog(value = "客户列表-id查询客户列")
	@ApiOperation(value="客户列表-id查询客户列", notes="客户列表-id查询客户列")
	@GetMapping(value = "/queryCustomersVoAllById")
	public Result<?> queryCustomersVoAllById(@RequestParam(name="id",required=true) Integer id) {
		CustomersVo customers =custMapper.queryCustomersVoAllById(id);
		if(customers==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(customers);
	}
	/**
	 * 添加
	 *
	 * @param customers
	 * @return
	 */
	@AutoLog(value = "客户表-添加")
	@ApiOperation(value="客户表-添加", notes="客户表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Customers customers,HttpServletRequest req) {
		if(customers.getProjectName() == null) {
			customers.setProjectName("");
		}
		QueryWrapper<Customers> deviceQuery = new QueryWrapper<Customers>();
		deviceQuery.eq("name", customers.getName().trim());
		deviceQuery.eq("ifnull(del_flag,0)", "0");
		List<Customers> list = this.customersService.list(deviceQuery);
		if(list != null && list.size() > 0){
			return Result.error("添加失败，客户名【"+customers.getName()+"】重复");
		}
		
//		DeviceVo device = deviceMapper.findDeviceCustByNamePreject(
//				customers.getName().trim(), 
//				customers.getProjectName().trim());
//		if(device!=null){
//			return Result.error("添加失败"+customers.getName()+"-"+customers.getProjectName()+"重复");
//		}
//		if(StrUtil.isNotBlank(customers.getProjectName())){
//			customers.setName(customers.getName()+"-"+customers.getProjectName());
//		}
//		List<DeviceVo> deviceCust = deviceMapper.findDeviceCust(customers.getName());
//		if(deviceCust.size()>0){
//			return Result.error("添加失败，客户名："+customers.getName()+"重复");
//		}
//		customers.setName()
//		if(StrUtil.isNotBlank(customers.getProjectName())){
//			customers.setName(customers.getName()+"-"+customers.getProjectName());
//		}
		customersService.save(customers);
		// 添加即自动授权
		Distribution distribution = new Distribution();
		distribution.setCustomerId(customers.getId().toString());
		distribution.setUserId(super.getLoginUser(req).getId());
		distributionService.save(distribution);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param customers
	 * @return
	 */
	@AutoLog(value = "客户表-编辑")
	@ApiOperation(value="客户表-编辑", notes="客户表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Customers customers) {
		customersService.updateById(customers);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "客户表-通过id删除")
	@ApiOperation(value="客户表-通过id删除", notes="客户表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
//		customersService.removeById(id);
//		deviceMapper.deleteByCustomerId(id);
		Customers entity = this.customersService.getById(id);
		entity.setDelFlag(1);
		customersService.updateById(entity);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "客户表-批量删除")
	@ApiOperation(value="客户表-批量删除", notes="客户表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		for(String id: ids.split(",")){
			customersService.removeById(id);
			deviceMapper.deleteByCustomerId(id);
		}
//		this.customersService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "客户表-通过id查询")
	@ApiOperation(value="客户表-通过id查询", notes="客户表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		Customers customers = customersService.getById(id);
		return Result.ok(customers);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param customers
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, Customers customers) {
		return super.exportXls(request, customers, Customers.class, "客户表");
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
		return super.importExcel(request, response, Customers.class);
	}

	/**
	 * id查询设备的数量
	 *
	 * @param customers
	 *
	 * @return
	 */
	@AutoLog(value = "根据id查客户设备报警总数量")
	@ApiOperation(value = "根据id查客户设备报警总数量", notes = "根据id查客户设备报警总数量")
	@GetMapping(value = "/queryDeviceAlarmById")
	public Result<?> queryDeviceAlarmById(@RequestParam(name = "id", required = true) Integer id) {
		int Customercount = custMapper.queryDeviceAlarmById(id);
		if (Customercount == 0) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(Customercount);
	}




	@AutoLog(value = "客户列表-id查询省份客户列")
	@ApiOperation(value="客户列表-id查询省份客户列", notes="客户列表-id查询省份客户列")
	@GetMapping(value = "/queryOneVoAllById")
	public Result<?> queryOneVoAllById(@RequestParam(name="pid",required=true) Integer pid,
									   @RequestParam(name="uid",required=true) String uid) {
		List<LoadCustomersVo> customers =custMapper.queryOneVoAllById(pid,uid);
		if(customers==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(customers);
	}


	@AutoLog(value = "客户列表-id查询客户列")
	@ApiOperation(value="客户列表-id查询客户列", notes="客户列表-id查询客户列")
	@GetMapping(value = "/queryCustomersVoAllBycustomerId")
	public Result<?> queryCustomersVoAllBycustomerId(@RequestParam(name="customerId",required=true) Integer customerId) {
		List<LoadCustomersVo> customers =custMapper.queryCustomersVoAllBycustomerId(customerId);
		if(customers==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(customers);
	}


	@AutoLog(value = "客户列表-id查询城市客户列")
	@ApiOperation(value="客户列表-id查询城市客户列", notes="客户列表-id查询城市客户列")
	@GetMapping(value = "/queryTowVoAllById")
	public Result<?> queryOneVoAllById(
			@RequestParam(name="pid",defaultValue= "0") Integer pid,
			@RequestParam(name="cid", defaultValue= "0") Integer cid,
			@RequestParam(name="customerId",defaultValue= "0") Integer customerId,
			@RequestParam(name="uid",required=true) String uid
	) {
		List<LoadCustomersVo> customers = null;
		if("0".equals(pid.toString())) {
			customers = distributionService.findAllByUserId(uid);
			for (LoadCustomersVo ss: customers) {
				//根据客户id查找区域id
				Customers customerById = customersService.getById(ss.getId());
				Integer oneAreaId = customerById.getOneAreaId();//一级id
				Area one = areaService.getById(oneAreaId);
				Integer twoAreaId = customerById.getTwoAreaId();//二级id
				Area two = areaService.getById(twoAreaId);
				//将其放入LoadCustomersVo
				ss.setOneAreaName(one.getAreaName());
				ss.setTwoAreaName(two.getAreaName());
				List<Integer> ids = deviceMapper.findDeviceIdsByCid(ss.getId(),
						uid);
				ss.setCount(ids.size());
			}
			System.out.println(customers);
			return Result.ok(customers);
		}
		if("0".equals(cid.toString())) {
			customers =custMapper.queryOneVoAllById(pid,uid);
			for (LoadCustomersVo ss: customers) {
				//根据客户id查找区域id
				Customers customerById = customersService.getById(ss.getId());
				Integer oneAreaId = customerById.getOneAreaId();//一级id
				Area one = areaService.getById(oneAreaId);
				Integer twoAreaId = customerById.getTwoAreaId();//二级id
				Area two = areaService.getById(twoAreaId);
				//将其放入LoadCustomersVo
				ss.setOneAreaName(one.getAreaName());
				ss.setTwoAreaName(two.getAreaName());
				List<Integer> ids = deviceMapper.findDeviceIdsByCid(ss.getId(), uid);
				ss.setCount(ids.size());
			}
			System.out.println(customers);
			return Result.ok(customers);
		}
		if(!"0".equals(customerId.toString())) {
			customers = custMapper.queryCustomersVoAllBycustomerId(customerId);
			for (LoadCustomersVo ss: customers) {
				//根据客户id查找区域id
				Customers customerById = customersService.getById(customerId);
				ss.setId(customerId);
				Integer oneAreaId = customerById.getOneAreaId();//一级id
				Area one = areaService.getById(oneAreaId);
				Integer twoAreaId = customerById.getTwoAreaId();//二级id
				Area two = areaService.getById(twoAreaId);
				//将其放入LoadCustomersVo
				ss.setOneAreaName(one.getAreaName());
				ss.setTwoAreaName(two.getAreaName());
				List<Integer> ids = deviceMapper.findDeviceIdsByCid(ss.getId(), uid);
				ss.setCount(ids.size());
			}
			System.out.println(customers);
			return Result.ok(customers);
		}else {
			customers =custMapper.queryTowVoAllById(pid,cid,uid);
			for (LoadCustomersVo ss: customers) {
				//根据客户id查找区域id
				Customers customerById = customersService.getById(ss.getId());
				Integer oneAreaId = customerById.getOneAreaId();//一级id
				Area one = areaService.getById(oneAreaId);
				Integer twoAreaId = customerById.getTwoAreaId();//二级id
				Area two = areaService.getById(twoAreaId);
				//将其放入LoadCustomersVo
				ss.setOneAreaName(one.getAreaName());
				ss.setTwoAreaName(two.getAreaName());
				List<Integer> ids = deviceMapper.findDeviceIdsByCid(ss.getId(), uid);
				ss.setCount(ids.size());
			}
		}
//		for (LoadCustomersVo customersVo: customers) {
//			if(StrUtil.isNotBlank(customersVo.getProjectName())){
//				customersVo.setName(customersVo.getName()+"-"+customersVo.getProjectName());
//			}
//		}

		if(customers==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(customers);
	}




	/**
	 * 根据客户id查询是否有楼层图纸
	 * @param cid
	 * @return
	 */
	@GetMapping("customerIfFloorMapByCid")
	@ApiOperation(value="根据客户id查询是否有楼层图纸", notes="根据客户id查询是否有楼层图纸")
	public Result<?> customerIfFloorMapByCid(@RequestParam("cid") Integer cid) {

		Boolean b= customersService.customerIfFloorMapByCid(cid);

		return Result.ok(b);
	}

}
