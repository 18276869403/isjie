package org.jeecg.modules.demo.monitor.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.monitor.entity.Customers;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.entity.FloorPlan;
import org.jeecg.modules.demo.monitor.entity.TestRecord;
import org.jeecg.modules.demo.monitor.mapper.DistributionMapper;
import org.jeecg.modules.demo.monitor.mapper.TestRecordVoMapper;
import org.jeecg.modules.demo.monitor.service.ICustomersService;
import org.jeecg.modules.demo.monitor.service.IDevicePlaceService;
import org.jeecg.modules.demo.monitor.service.IDeviceService;
import org.jeecg.modules.demo.monitor.service.IFloorPlanService;
import org.jeecg.modules.demo.monitor.service.ITestRecordService;
import org.jeecg.modules.demo.monitor.service.ITestRecordVoService;
import org.jeecg.modules.demo.monitor.vo.DetectorDTO;
import org.jeecg.modules.demo.monitor.vo.DetectorDTOout;
import org.jeecg.modules.demo.monitor.vo.TestRecordForPageVO;
import org.jeecg.modules.demo.monitor.vo.TestRecordVo;
import org.springframework.beans.BeanUtils;
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
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 监测记录表
 * @Author: jeecg-boot
 * @Date: 2019-12-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "监测记录表")
@RestController
@RequestMapping("/monitor/testRecord")
public class TestRecordController extends JeecgController<TestRecord, ITestRecordService> {
	@Autowired
	private ITestRecordService testRecordService;

	@Autowired
	ITestRecordVoService ITestRecordVoService;
	
	@Autowired
	private TestRecordVoMapper testRecordVoMapper;

	@Autowired
	private DistributionMapper distributionMapper;

	@Autowired
	private IDeviceService deviceService;
	
	@Autowired
	private IFloorPlanService floorPlanService;

	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	private ICustomersService customersService;
	@Autowired
	private IDevicePlaceService devicePlaceService;

	private static Map<String,String> map1;

	static{
		map1 = new HashMap<>();
		map1.put("探测器","1");
		map1.put("控制器","2");
		map1.put("输出模块","3");
		map1.put("1","探测器");
		map1.put("2","控制器");
		map1.put("3","输出模块");
	}



	/**
	 * 监测记录联合查询-分页列表查询
	 *
	 * @param testRecordVo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "监测记录联合查询-分页列表查询")
	@ApiOperation(value = "监测记录联合查询-分页列表查询", notes = "监测记录联合查询-分页列表查询")
	@GetMapping(value = "/queryTestAll")
	public Result<?> queryTestAll(TestRecordVo testRecordVo,
								  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<TestRecordVo> queryWrapper = new QueryWrapper<TestRecordVo>().orderByDesc("t.Acquisition_time");
//		String header = req.getHeader("X-Access-Token");
//		ShiroRealm shiroRealm = new ShiroRealm();
//		String username = JwtUtil.getUsername(header);
//		LoginUser sysUser = sysBaseAPI.getUserByName(username);
		
//		QueryWrapper<Distribution> disWrapper = new QueryWrapper<Distribution>();
//		disWrapper.eq("User_Id", sysUser.getId());
//		List<Distribution> disList = distributionMapper.selectList(disWrapper);
//		if(disList != null) {
//			Set<String> customerIds = new HashSet<String>();
//			for(Distribution dis : disList) {
//				customerIds.add(dis.getCustomerId());
//			}
//			queryWrapper.in("t.CustomerId", customerIds);
//		}
//		queryWrapper.eq("dis.User_Id",sysUser.getId());
		super.appendDeviceAreaAuth(req, queryWrapper);
		//一二级区域查询
		if (StrUtil.isNotBlank(testRecordVo.getOneAreaName())) {
			queryWrapper.eq("c.One_area_id", testRecordVo.getOneAreaName());
		}
		if (StrUtil.isNotBlank(testRecordVo.getTwoAreaName())) {
			queryWrapper.eq("c.Two_area_id", testRecordVo.getTwoAreaName());
		}
		// 客户名称模糊查询
		if (StrUtil.isNotBlank(testRecordVo.getName())) {
			queryWrapper.like("c.name", testRecordVo.getName());
		}
		// 设备编号模糊查询
		if (StrUtil.isNotBlank(testRecordVo.getDeviceNum())) {
			queryWrapper.like("d.Device_num", testRecordVo.getDeviceNum());
		}
		// 设备IMEI模糊查询
		if (StrUtil.isNotBlank(testRecordVo.getTestDeviceimei())) {
			queryWrapper.like("t.Test_deviceImei", testRecordVo.getTestDeviceimei());
		}
		//设备类型
		if (StrUtil.isNotBlank(testRecordVo.getDeviceType())) {
			queryWrapper.like("d.Device_type", testRecordVo.getDeviceType());
		}
		// 时间段查询
		if (StrUtil.isNotBlank(req.getParameter("acquisitionTime_begin"))) {
			if (StrUtil.isNotBlank(req.getParameter("acquisitionTime_end"))) {
				queryWrapper.between("Acquisition_time", req.getParameter("acquisitionTime_begin"),
						req.getParameter("acquisitionTime_end"));
			} else {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				queryWrapper.between("Acquisition_time", req.getParameter("acquisitionTime_begin"),
						df.format(new Date()));
			}
		} else {
			if (StrUtil.isNotBlank(req.getParameter("acquisitionTime_end"))) {
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.YEAR, -200);
				Date y = c.getTime();
				queryWrapper.between("Acquisition_time", y, req.getParameter("acquisitionTime_end"));
			}
		}
		// 未设置过滤时间，仅查询7天内
		if(StringUtils.isEmpty(req.getParameter("acquisitionTime_begin")) 
				&& StringUtils.isEmpty(req.getParameter("acquisitionTime_end"))) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.DATE, -7);
			Date y = c.getTime();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			queryWrapper.between("Acquisition_time", 
					df.format(y),
					df.format(new Date()));
		}
		Page<TestRecordVo> page = new Page<>(pageNo, pageSize);
		IPage<TestRecordVo> iPage = ITestRecordVoService.queryTestAll(page, queryWrapper);
		return Result.ok(iPage);
	}


	/**
	 * 实时监测记录查询-分页查询
	 *
	 * @param testRecordVo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "实时监测记录查询-分页查询")
	@ApiOperation(value = "实时监测记录查询-分页查询", notes = "实时监测记录查询-分页查询")
	@GetMapping(value = "/queryTest")
	public Result<?> queryTest(
			TestRecordForPageVO testRecordVo,
		   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
		   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, 
		   HttpServletRequest req) {

		TestRecordForPageVO testRecordForPageVO = new TestRecordForPageVO();
		testRecordForPageVO.setPageNo((pageNo-1)*pageSize);//开始值
		testRecordForPageVO.setPageSize(pageSize);//每页数量
		//将vo转换为pagevo
		testRecordForPageVO.setUserId(testRecordVo.getUserId());//放入用户id
		testRecordForPageVO.setOneAreaId(testRecordVo.getOneAreaId());//客户id
		// 客户编号查询
		if (testRecordVo.getCustomerId()!=null&&!StrUtil.isNotBlank(testRecordVo.getTestDeviceImei())) {
			if(testRecordVo.getUserId()== null||("".equals(testRecordVo.getUserId()))){
				String userId = distributionMapper.queryBycid(testRecordVo.getCustomerId());
				testRecordForPageVO.setUserId(userId);
			}
		}
		testRecordForPageVO.setTestDeviceImei(testRecordVo.getTestDeviceImei());//imei
//		testRecordForPageVO.setTestAddress(testRecordVo.getTestAddress());//address
		testRecordForPageVO.setTwoAreaId(testRecordVo.getTwoAreaId());//一级区域id
		testRecordForPageVO.setCustomerId(testRecordVo.getCustomerId());//二级区域id
		// 设备类型
		if (StrUtil.isNotBlank(testRecordVo.getDeviceType())) {
			if(NumberUtils.isNumber(testRecordVo.getDeviceType())){
				testRecordForPageVO.setDeviceType(testRecordVo.getDeviceType());//设备类型
			}else{
				testRecordForPageVO.setDeviceType(map1.get(testRecordVo.getDeviceType()));//设备类型
			}
		}
		// 设备编号模糊查询
		if (StrUtil.isNotBlank(testRecordVo.getDeviceNum())) {
			testRecordForPageVO.setDeviceNum("%"+testRecordVo.getDeviceNum()+"%");//设备位号
		}
		// 时间段查询
		if (StrUtil.isNotBlank(req.getParameter("acquisitionTime_begin"))){
			testRecordForPageVO.setAcquisitionTime_begin(req.getParameter("acquisitionTime_begin"));//开始时间
		}
		if (StrUtil.isNotBlank(req.getParameter("acquisitionTime_end"))){
			testRecordForPageVO.setAcquisitionTime_end(req.getParameter("acquisitionTime_end"));//结束时间
		}
		String temp;
		if (StrUtil.isNotBlank(temp = testRecordVo.getTestAddress())){
			testRecordForPageVO.setTestAddress("%" + temp + "%");//地址
		}
		if (StrUtil.isNotBlank(temp = testRecordVo.getDetectionTarget())){
			testRecordForPageVO.setDetectionTarget("%" + temp + "%");//目标
		}
		if (StrUtil.isNotBlank(temp = testRecordVo.getTstatus())){
			testRecordForPageVO.setTstatus("%" + temp + "%");//状态
		}
		//查询总条数

//		testRecordForPageVO.setPageSize(list.size());
		List<TestRecordVo> testRecordVo2=new ArrayList<>();
		Page<TestRecordVo> page = new Page<>(pageNo, pageSize);
		page.setCurrent(pageNo);//所在页
		page.setSize(pageSize);//每页显示条数
		if(testRecordVo.getDeviceId() == null){
			List<Device> list = deviceService.list();
			testRecordVo2 = testRecordVoMapper.queryTestByPage3(testRecordForPageVO);
			testRecordForPageVO.setPageNo(0);
			testRecordForPageVO.setPageSize(list.size());
			List<TestRecordVo> testRecordVos1 = testRecordVoMapper.queryTestByPage3(testRecordForPageVO);
			page.setTotal(testRecordVos1.size());//设置总条数
		} else {
			// 查询具体设备的历史采集记录
			// 
			Device device = this.deviceService.getById(testRecordVo.getDeviceId());
			if(device != null) {
				Page<TestRecord> recordPage = new Page<TestRecord>(pageNo, pageSize);
				QueryWrapper<TestRecord> query = new QueryWrapper<TestRecord>();
				query.eq("deviceId", device.getId());
				// 时间段查询
				if (StrUtil.isNotBlank(temp = req.getParameter("acquisitionTime_begin"))){
					query.gt("Acquisition_time", temp + " 00:00:00");//开始时间
				}
				if (StrUtil.isNotBlank(temp = req.getParameter("acquisitionTime_end"))){
					query.lt("Acquisition_time", temp + " 23:59:59");//结束时间
				}
				if (StrUtil.isNotBlank(temp = testRecordVo.getTestAddress())){
					testRecordForPageVO.setTestAddress("%" + temp + "%");//地址
				}
//				if (StrUtil.isNotBlank(temp = testRecordVo.getDetectionTarget())){
//					testRecordForPageVO.setDetectionTarget("%" + temp + "%");//目标
//				}
				if (StrUtil.isNotBlank(temp = testRecordVo.getTstatus())){
					query.like("T_status", "%" + temp + "");//状态
				} 
				
				
				recordPage.addOrder(OrderItem.desc("id"));
				IPage<TestRecord> recordPageList = this.testRecordService.page(recordPage, query);
			
				page.setTotal(recordPageList.getTotal());
				if(recordPageList.getTotal() > 0) {
					List<Integer> floorIds = this.deviceService.findFloorByDeviceId(device.getId());
					FloorPlan floorPlan = null;
					Customers customer  = this.customersService.getById(device.getCustomerId());
					if(floorIds != null && floorIds.size() > 0) {
						floorPlan = this.floorPlanService.getById(floorIds.get(0));
					}
					
					for(TestRecord tr : recordPageList.getRecords()) {
						TestRecordVo trv = new TestRecordVo();
						trv.setAcquisitionTime(tr.getAcquisitionTime());
						trv.setCustomerId(device.getCustomerId());
						trv.setDeviceId(device.getId());
						trv.setDetectionTarget(device.getDetectionTarget());
						trv.setDeviceImei(device.getDeviceImei());
						trv.setDeviceNum(device.getDeviceNum());
						trv.setDeviceType(device.getDeviceType());
						if(floorPlan != null) {
							trv.setFloorName(floorPlan.getFloorName());
						}
						trv.setRanges(String.valueOf(device.getRanges()));
						trv.setRanges1(String.valueOf(device.getRanges()) + device.getUnit());
						trv.setTestAddress(device.getAddressNumber());
						trv.setTestDeviceimei(device.getDeviceImei());
						trv.setTestDevicetype(device.getDeviceType());
						trv.setTestIccid(device.getIccid());
						if(tr.getTestPv() == null) {
							trv.setTestPv("");
						} else {
							String testPv = String.valueOf(tr.getTestPv().multiply(device.getReductionCoefficient()));
//							if(StringUtils.isNotEmpty(device.getUnit())) {
//								testPv += device.getUnit();
//							}
							trv.setTestPv(testPv);
						}
						trv.setTStatus(tr.getTStatus());
						trv.setName(customer.getName());
						trv.setDeviceModel(device.getDeviceModel());
						testRecordVo2.add(trv);
					}
				}
			}
			
//			List<TestRecordVo> testRecordVos = testRecordVoMapper.queryTestByPage(testRecordForPageVO);
//			Integer listCount = testRecordVoMapper.findCount(testRecordForPageVO);
//			page.setTotal(listCount);//设置总条数
////			List<TestRecordVo> testRecordVos2 = testRecordVoMapper.queryTestByPage(testRecordForPageVO);
//			for(int i=0;i<testRecordVos.size();i++){
//				TestRecordVo testRecordVo1 = testRecordVoMapper.queryTestByPage1(testRecordVos.get(i).getId());
//				testRecordVo2.add(testRecordVo1);
//			}
		}

		page.setSearchCount(true);//设置查询情况
		page.setRecords(testRecordVo2);//放入此页数据
		page.setPages((page.getTotal()+pageSize-1)/pageSize);//放入总页数
		return Result.ok(page);
	}
	/**
	 * 分页列表查询
	 *
	 * @param testRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "监测记录表-分页列表查询")
	@ApiOperation(value = "监测记录表-分页列表查询", notes = "监测记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TestRecord testRecord,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<TestRecord> queryWrapper = QueryGenerator.initQueryWrapper(testRecord, req.getParameterMap());

		Page<TestRecord> page = new Page<TestRecord>(pageNo, pageSize);
		IPage<TestRecord> pageList = testRecordService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param testRecord
	 * @return
	 */
	//@AutoLog(value = "监测记录表-添加")
	@ApiOperation(value = "监测记录表-添加", notes = "监测记录表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TestRecord testRecord) {
		testRecordService.save(testRecord);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param testRecord
	 * @return
	 */
	//@AutoLog(value = "监测记录表-编辑")
	@ApiOperation(value = "监测记录表-编辑", notes = "监测记录表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TestRecord testRecord) {
		testRecordService.updateById(testRecord);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "监测记录表-通过id删除")
	@ApiOperation(value = "监测记录表-通过id删除", notes = "监测记录表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		testRecordService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	//@AutoLog(value = "监测记录表-批量删除")
	@ApiOperation(value = "监测记录表-批量删除", notes = "监测记录表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.testRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "监测记录表-通过id查询")
	@ApiOperation(value = "监测记录表-通过id查询", notes = "监测记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		TestRecord testRecord = testRecordService.getById(id);
		return Result.ok(testRecord);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param testRecord
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, TestRecord testRecord) {
		return super.exportXls(request, testRecord, TestRecord.class, "监测记录表");
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
		return super.importExcel(request, response, TestRecord.class);
	}
	/**
	 * 导出excel
	 *
	 * @param request
	 * @param recordVo
	 */
	//@AutoLog(value = "监测记录联合查询表-Excel导出")
	@ApiOperation(value = "监测记录联合查询表-Excel导出", notes = "监测记录联合查询表-Excel导出")
	@GetMapping(value = "/exportXlsTestAll")
	public Result<List<TestRecordVo>> exportXls(HttpServletRequest request,
												TestRecordVo recordVo,
												@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
												@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

//		TestRecordForPageVO testRecordForPageVO = new TestRecordForPageVO();
		// Step.1 组装查询条件
		QueryWrapper<TestRecordVo> queryWrapper =new QueryWrapper<TestRecordVo>().groupBy("d.device_imei","d.address_number").orderByDesc("d.BackUp1","d.Device_num");
		Result<List<TestRecordVo>> result = new Result<List<TestRecordVo>>();
		//用户id
		if (recordVo.getUserId() != null&&!("".equals(recordVo.getUserId()))){
//			queryWrapper.eq("dis.User_id", recordVo.getUserId());
			super.appendDeviceAreaAuth(request, queryWrapper);
		}
		//一级区域id
		if (recordVo.getOneAreaId() != null) {
			queryWrapper.eq("c.One_area_id", recordVo.getOneAreaId());
		}
		//二级区域id
		if (recordVo.getTwoAreaId()!= null) {
			queryWrapper.eq("c.Two_area_id", recordVo.getTwoAreaId());
		}
		// 客户名称模糊查询
		if (StrUtil.isNotBlank(recordVo.getName())) {
			queryWrapper.like("c.name", recordVo.getName());
		}
		// 客户编号查询
		if (recordVo.getCustomerId()!=null&&!StrUtil.isNotBlank(recordVo.getUserId())) {
			queryWrapper.eq("d.Customer_id", recordVo.getCustomerId());
			if(recordVo.getUserId()== null||("".equals(recordVo.getUserId()))){
				String userId = distributionMapper.queryBycid(recordVo.getCustomerId());
				recordVo.setUserId(userId);
				queryWrapper.eq("dis.User_id", recordVo.getUserId());
			}
		}
		// 楼层名称模糊查询
		if (StrUtil.isNotBlank(recordVo.getFloorName())) {
			queryWrapper.like("f.Floor_name", recordVo.getFloorName());
		}
		// 设备编号模糊查询
		if (StrUtil.isNotBlank(recordVo.getDeviceNum())) {
			queryWrapper.like("d.Device_num", recordVo.getDeviceNum());
		}
		if(recordVo.getDeviceId() != null) {
			queryWrapper.eq("d.id", recordVo.getDeviceId());
		}
		// 设备IMEI模糊查询
//		if (StrUtil.isNotBlank(recordVo.getTestDeviceimei())) {
//			queryWrapper.like("t.Test_deviceImei", recordVo.getTestDeviceimei());
//			TestRecordForPageVO testRecordForPageVO = new TestRecordForPageVO();
//			testRecordForPageVO.setPageNo((pageNo-1)*pageSize);//开始值
//			testRecordForPageVO.setPageSize(pageSize);//每页数量
//			testRecordForPageVO.setTestDeviceImei(recordVo.getTestDeviceimei());//imei
//			testRecordForPageVO.setTestAddress(recordVo.getTestAddress());//address
//			List<TestRecordVo> testRecordVos = testRecordVoMapper.queryTestByPage(testRecordForPageVO);
//			ArrayList<TestRecordVo> testRecordVos1 = new ArrayList<>();
//			for(int i=0;i<testRecordVos.size();i++){
//				TestRecordVo testRecordVo1 = testRecordVoMapper.queryTestByPage1(testRecordVos.get(i).getId());
//				String alarmDeviceType = testRecordVo1.getDeviceType();
//				//判断设备类型
//				if (alarmDeviceType.equals("1")) {
//					testRecordVo1.setDeviceType("探测器");
//				}else if (alarmDeviceType.equals("2")) {
//					testRecordVo1.setDeviceType("控制器");
//				}else if (alarmDeviceType.equals("3")) {
//					testRecordVo1.setDeviceType("输出模块");
//				}
//				testRecordVos1.add(testRecordVo1);
//			}
//			result.setMessage("操作成功!");
//			result.setSuccess(true);
//			result.setResult(testRecordVos1);
//			return result;
//		}
//		if (StrUtil.isNotBlank(alarmRecord.getTestAddress())) {
//			queryWrapper.eq("t.Test_address", alarmRecord.getTestAddress());
//		}
		//设备类型
		if (StrUtil.isNotBlank(recordVo.getDeviceType())) {

			if(NumberUtils.isNumber(recordVo.getDeviceType())){
				queryWrapper.like("d.Device_type", recordVo.getDeviceType());
			}else{
				queryWrapper.like("d.Device_type", map1.get(recordVo.getDeviceType()));
			}
		}
		// 设备地址编号查询

		// 时间段查询
//		if (StrUtil.isNotBlank(request.getParameter("acquisitionTime_begin"))) {
//			if (StrUtil.isNotBlank(request.getParameter("acquisitionTime_end"))) {
//				queryWrapper.between("Acquisition_time", request.getParameter("acquisitionTime_begin"),
//						request.getParameter("acquisitionTime_end"));
//			} else {
//				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				queryWrapper.between("Acquisition_time", request.getParameter("acquisitionTime_begin"),
//						df.format(new Date()));
//			}
//		} else {
//			if (StrUtil.isNotBlank(request.getParameter("acquisitionTime_end"))) {
////				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Calendar c = Calendar.getInstance();
//				c.setTime(new Date());
//				c.add(Calendar.YEAR, -200);
//				Date y = c.getTime();
//				queryWrapper.between("Acquisition_time", y, request.getParameter("acquisitionTime_end"));
//			}
//		}

		String temp;
		if (StrUtil.isNotBlank(temp = request.getParameter("testAddress"))){
			queryWrapper.like("d.Address_number", "%" + temp + "%");//地址
		}
		if (StrUtil.isNotBlank(temp = request.getParameter("detectionTarget"))){
			queryWrapper.like("d.Detection_target", "%" + temp + "%");//目标
		}
		if (StrUtil.isNotBlank(temp = request.getParameter("tstatus"))){
			queryWrapper.like("d.status_type", "%" + temp + "%");//状态
		}
		// Step.2 获取导出数据
//		queryWrapper.last(" limit " + ((pageNo - 1) * pageSize)  + "," + pageSize + " ");
		List<TestRecordVo> pageList = testRecordVoMapper.exportXlsTestAll(queryWrapper);
		for (int i = 0; i < pageList.size(); i++) {
			TestRecordVo testRecordVo = pageList.get(i);
			if (testRecordVo != null) {
				String alarmDeviceType = testRecordVo.getDeviceType();
				//判断设备类型
				if (alarmDeviceType.equals("1")) {
					testRecordVo.setDeviceType("探测器");
				}else if (alarmDeviceType.equals("2")) {
					testRecordVo.setDeviceType("控制器");
				}else if (alarmDeviceType.equals("3")) {
					testRecordVo.setDeviceType("输出模块");
				}
			}
		}
		result.setMessage("操作成功!");
		result.setSuccess(true);
		result.setResult(pageList);

		return result;
	}


	/**
	 * 根据设备型号...查询测得的数据
	 * @param detectorDTO
	 * @return
	 */
	@PostMapping("/queryFloorDetector")
	@ApiOperation(value = "根据设备型号...查询测得的数据", notes = "根据设备型号...查询测得的数据")
	public Result<?> queryFloorDetector(@RequestBody DetectorDTO detectorDTO) {

		DetectorDTOout detectorDTOout=testRecordService.queryFloorDetector(detectorDTO);
		if (detectorDTOout == null) {
			return Result.error("查询结果为空");
		}

		return Result.ok(detectorDTOout);
	}
	/**
	 * 导出excel
	 *
	 * @param request
	 * @param recordVo
	 */
	//@AutoLog(value = "单设备监测记录-Excel导出")
	@ApiOperation(value = "监测记录联合查询表-Excel导出", notes = "监测记录联合查询表-Excel导出")
	@GetMapping(value = "/exportDeviceXls")
	public Result<List<TestRecordVo>> exportDeviceXls(HttpServletRequest request,
					@RequestParam(name = "deviceId", required = true) Integer deviceId,
					@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
					@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		QueryWrapper<TestRecord> queryWrapper = new QueryWrapper<TestRecord>()
				.orderByDesc("Acquisition_time desc,id") ;
		queryWrapper.eq("deviceId", deviceId);
		// 时间段查询
		String temp;
		if (StrUtil.isNotBlank(temp = request.getParameter("acquisitionTime_begin"))){
			queryWrapper.gt("Acquisition_time", temp + " 00:00:00");//开始时间
		}
		if (StrUtil.isNotBlank(temp = request.getParameter("acquisitionTime_end"))){
			queryWrapper.lt("Acquisition_time", temp + " 23:59:59");//结束时间
		}
		if (StrUtil.isNotBlank(temp = request.getParameter("tstatus"))){
			queryWrapper.like("T_status", "%" + temp + "");//状态
		} 
		queryWrapper.last(" limit " + ((pageNo - 1) * pageSize)  + "," + pageSize + " ");
		List<TestRecord> pageList = testRecordService.list(queryWrapper);
		
		
		List<TestRecordVo> voList = new ArrayList<TestRecordVo>();
		Result<List<TestRecordVo>> result = new Result<List<TestRecordVo>>();
		if(pageList != null && pageList.size() > 0) {
			Device device = this.deviceService.getById(deviceId);
			Customers customer = this.customersService.getById(device.getCustomerId());
			FloorPlan floorPlan = null;
			List<FloorPlan> fpList = devicePlaceService.findFloorByDeviceId(deviceId, customer.getId());
			if(fpList != null && fpList.size() > 0) {
				floorPlan = fpList.get(0);
			}
			for (int i = 0; i < pageList.size(); i++) {
				TestRecord testRecordVo = pageList.get(i);
				TestRecordVo vo = new TestRecordVo();
				BeanUtils.copyProperties(testRecordVo, vo);
				BeanUtils.copyProperties(device, vo);
				BeanUtils.copyProperties(customer, vo);
				if(floorPlan != null) {
					BeanUtils.copyProperties(floorPlan, vo);
				}
				if (testRecordVo != null) {
					String alarmDeviceType = device.getDeviceType();
					//判断设备类型
					if (alarmDeviceType.equals("1")) {
						vo.setDeviceType("探测器");
					}else if (alarmDeviceType.equals("2")) {
						vo.setDeviceType("控制器");
					}else if (alarmDeviceType.equals("3")) {
						vo.setDeviceType("输出模块");
					}
				}
				if(device.getRanges() == null) {
					device.setRanges(BigDecimal.valueOf(0D));
				}
				if(device.getUnit() == null) {
					device.setUnit("");
				}
				vo.setRanges(device.getRanges().toString() + device.getUnit());
				if(testRecordVo.getTestPv() == null) {
					vo.setTestPv("");
				} else {
					String testPv = String.valueOf(testRecordVo.getTestPv().multiply(device.getReductionCoefficient()));
//					if(StringUtils.isNotEmpty(device.getUnit())) {
//						testPv += device.getUnit();
//					}
					vo.setTestPv(testPv);
				}
//				vo.setTestPv(testRecordVo.getTestPv().toString());
				voList.add(vo);
			}
		}
		result.setMessage("操作成功!");
		result.setSuccess(true);
		result.setResult(voList);

		return result;
	}
	/**
	 * 导出excel
	 *
	 * @param request
	 * @param recordVo
	 */
	//@AutoLog(value = "单设备监测记录-Excel导出")
	@ApiOperation(value = "监测记录联合查询表-Excel导出", notes = "监测记录联合查询表-Excel导出")
	@GetMapping(value = "/queryAnalytics")
	@ResponseBody
	public Result<Object> queryAnalytics( 
					@RequestParam(name = "dids", required = true) String dids,
					@RequestParam(name = "startTime", required = true) String startTime,
					@RequestParam(name = "endTime", required = true) String endTime,
					@RequestParam(name = "lastId", defaultValue = "0") Integer lastId) {
		QueryWrapper<TestRecord> queryWrapper = new QueryWrapper<TestRecord>();
		//queryWrapper.in("deviceId", Arrays.asList(dids.split(",")));
		queryWrapper = queryWrapper.select("id", "Acquisition_time", "Test_pv", "deviceId");
		// deviceId 放到java代码里过滤效率更高
//		queryWrapper.eq("deviceId", Integer.parseInt(dids));
		queryWrapper.between("Acquisition_time", startTime, endTime);
//		queryWrapper.gt("id", lastId);
		// 封装曲线数据
		List<TestRecord> recordList = testRecordService.list(queryWrapper);
		JSONArray data = new JSONArray();
		for(TestRecord tr : recordList) {
			if(!dids.equals(tr.getDeviceId().toString())) {
				continue;
			}
			JSONObject d = new JSONObject();
			d.put("acquisitionTime", tr.getAcquisitionTime());
			d.put("testPv", tr.getTestPv());
			d.put("id", tr.getId());
			data.add(d);
		}
		return Result.ok(data);
	}
}
