package org.jeecg.modules.demo.monitor.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.monitor.DeviceTypeConf;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.entity.DeviceArea;
import org.jeecg.modules.demo.monitor.mapper.DeviceMapper;
import org.jeecg.modules.demo.monitor.service.IDeviceAreaService;
import org.jeecg.modules.demo.monitor.service.IDeviceService;
import org.jeecg.modules.demo.monitor.util.SattusSplitUtil;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.jeecg.modules.demo.monitor.vo.DeviceCountDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceTypeCount;
import org.jeecg.modules.demo.monitor.vo.DeviceVo;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date: 2019-12-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "设备表")
@RestController
@RequestMapping("/monitor/device")
public class DeviceController extends JeecgController<Device, IDeviceService> {
	@Autowired
	private IDeviceService deviceService;
	@Autowired
	private IDeviceAreaService deviceAreaService;
	@Autowired
	DeviceMapper devMapper;
	@Autowired
	RedisUtil redis;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	private static Map<String,String> map;
	static{
		map = new HashMap<>();
		map.put("1","探测器");
		map.put("2","控制器");
		map.put("3","输出模块");
	}

	/**
	 * 分页列表查询
	 *
	 * @param device
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "设备表-分页列表查询")
	@ApiOperation(value = "设备表-分页列表查询", notes = "设备表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(Device device, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<Device> queryWrapper = QueryGenerator.initQueryWrapper(device, req.getParameterMap());
		Page<Device> page = new Page<Device>(pageNo, pageSize);
		IPage<Device> pageList = deviceService.page(page, queryWrapper);
		return Result.ok(pageList);
	}



	//@AutoLog(value = "设备表-查询是否有报警设备")
	@ApiOperation(value = "设备表-查询是否有报警设备", notes = "设备表-查询是否有报警设备")
	@GetMapping(value = "/queryAlarmdevice")
	public Result<?> queryAlarmdevice(@RequestParam(name = "userId") String userId,
								   @RequestParam(name = "customerId", defaultValue = "0") Integer customerId) {
		if("".equals(userId)||"0".equals(userId)){
			userId = null;
		}
//		SimpleDateFormat ssf = new SimpleDateFormat("MM-dd HH:mm");
		List<Device> devices = deviceService.queryAlarmdevice(userId,customerId);
		for(Device device : devices){
			device.setStatusType(SattusSplitUtil.getStatus(device.getStatusType()));
			device.setDeviceType(map.get(device.getDeviceType()));
			device.setBackup1(device.getBackup1().substring(5,16));
			if(StrUtil.isNotBlank(device.getDeviceNum())&&StrUtil.isNotBlank(device.getDevicePositionnum())){
				device.setDeviceNum(device.getDeviceNum()+"/"+device.getDevicePositionnum());
			}else if(!StrUtil.isNotBlank(device.getDeviceNum())){
				device.setDeviceNum("");
			}
		}
		if(devices.size()==0)return Result.ok("无报警设备");
		return Result.ok(devices);
	}

//	//@AutoLog(value = "设备表-楼层图页面设备查找")
//	@ApiOperation(value = "设备表-楼层图页面设备查找", notes = "设备表-楼层图页面设备查找")
//	@GetMapping(value = "/list")
//	public Result<?> querydeviceBydeviceNum(@Param("deviceNum") Integer deviceNum, HttpServletRequest req) {
////		Device device
////		return Result.ok(device);
//	}

	/**
	 * 添加
	 *
	 * @param device
	 * @return
	 */
	//@AutoLog(value = "设备表-添加")
	@ApiOperation(value = "设备表-添加", notes = "设备表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Device device) {
		CaiJiVO caiJiVO = new CaiJiVO();
		caiJiVO.setImei(device.getDeviceImei());
		caiJiVO.setAdd(device.getAddressNumber());
		caiJiVO.setIccid(device.getIccid());
		caiJiVO.setType(DeviceTypeConf.getType());
		Device deviceByImeiAndAdd = deviceService.findDeviceByImeiAndAdd(caiJiVO);
		if(deviceByImeiAndAdd != null){
			return Result.error("添加失败，设备imei+add重复！");
		}
		deviceService.save(device);
		deviceService.saveFloorDevice(device);
    	String key = "allDeviceCache";
    	this.redis.del(key);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param device
	 * @return
	 */
	//@AutoLog(value = "设备表-编辑")
	@ApiOperation(value = "设备表-编辑", notes = "设备表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Device device) {
		CaiJiVO caiJiVO = new CaiJiVO();
		caiJiVO.setImei(device.getDeviceImei());
		caiJiVO.setAdd(device.getAddressNumber());
		caiJiVO.setIccid(device.getIccid());
		caiJiVO.setType(DeviceTypeConf.getType());
		Device dbDevice = deviceService.findDeviceByImeiAndAdd(caiJiVO);
		if(dbDevice != null 
				&& dbDevice.getId().intValue() != device.getId().intValue()){
			return Result.error("修改失败，设备imei+add重复！");
		}
		deviceService.updateById(device);
    	String key = "device#cache";
    	this.redis.deleteByPrex(key);
    	key = "allDeviceCache";
    	this.redis.del(key);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "设备表-通过id删除")
	@ApiOperation(value = "设备表-通过id删除", notes = "设备表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		deviceService.removeById(id);
		deviceService.deletefloordevice(id);
    	String key = "device#cache";
    	this.redis.deleteByPrex(key);
    	key = "allDeviceCache";
    	this.redis.del(key);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	//@AutoLog(value = "设备表-批量删除")
	@ApiOperation(value = "设备表-批量删除", notes = "设备表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.deviceService.removeByIds(Arrays.asList(ids.split(",")));
    	String key = "device#cache";
    	this.redis.deleteByPrex(key);
    	key = "allDeviceCache";
    	this.redis.del(key);
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "设备表-通过id查询")
	@ApiOperation(value = "设备表-通过id查询", notes = "设备表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		Device device = deviceService.getById(id);
		return Result.ok(device);
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
		boolean exportData = "true".equals(request.getParameter("exportData"));//是否为导出，下载模版仅保留三条类型对应的一条数据即可
		Map<String, DeviceVo> demoData = new HashMap<String, DeviceVo>();
		QueryWrapper<DeviceVo> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("d.create_time");
//		queryWrapper.last("limit 0,1");
		super.appendDeviceAreaAuth(request, queryWrapper);
		List<DeviceVo> pageList = devMapper.exportXls(queryWrapper);
		for (DeviceVo deviceVo: pageList) {
			if (deviceVo.getDeviceType().equals("1")){
				deviceVo.setDeviceType("探测器");
			}else if (deviceVo.getDeviceType().equals("2")){
				deviceVo.setDeviceType("控制器");
			}else if (deviceVo.getDeviceType().equals("3")){
				deviceVo.setDeviceType("输出模块");
			}
			demoData.put(deviceVo.getDeviceType(), deviceVo);
		}
		if(!exportData) {
			pageList.clear();
			for(String key : demoData.keySet()) {
				pageList.add(demoData.get(key));
			}
		}
		//导出文件名称
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		mv.addObject(NormalExcelConstants.FILE_NAME, "设备表");
		mv.addObject(NormalExcelConstants.CLASS, DeviceVo.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("设备列表数据", "导出人:"+user.getRealname(), "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
//		return super.exportXls(request, device, Device.class, "设备表");
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
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String,MultipartFile> entry : fileMap.entrySet()){
			MultipartFile file = entry.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<DeviceVo> deviceVoList = ExcelImportUtil.importExcel(file.getInputStream(), DeviceVo.class, params);
				//检查表格中是否有重复的imei+add
				for(int a = 0; a < deviceVoList.size(); a++){
					DeviceVo deviceExcel = deviceVoList.get(a);
					CaiJiVO caiJiVO = new CaiJiVO();
					caiJiVO.setImei(deviceExcel.getDeviceImei());
					caiJiVO.setAdd(deviceExcel.getAddressNumber());
					caiJiVO.setIccid(deviceExcel.getIccid());
					caiJiVO.setType(DeviceTypeConf.getType());
					
					Device deviceByImeiAndAdd = deviceService.findDeviceByImeiAndAdd(caiJiVO);
					if(deviceByImeiAndAdd != null){
						return Result.error("添加失败，设备"+deviceByImeiAndAdd.getDeviceImei()+"+"+deviceByImeiAndAdd.getAddressNumber()+"重复！");
					}
					for(int b = a + 1; b < deviceVoList.size(); b++) {
						DeviceVo nextDeviceExcel = deviceVoList.get(b);
						if(deviceExcel.getDeviceImei().equals(nextDeviceExcel.getDeviceImei()) &&
								deviceExcel.getAddressNumber().equals(nextDeviceExcel.getAddressNumber())) {
							if( DeviceTypeConf.isCom() &&
									StringUtils.isNotEmpty(deviceExcel.getIccid()) && 
									deviceExcel.getIccid().equals(nextDeviceExcel.getIccid())) {
								return Result.error("添加失败，设备"+nextDeviceExcel.getDeviceImei() + "+" + 
										nextDeviceExcel.getIccid() + "+" + 
										nextDeviceExcel.getAddressNumber() + 
										"重复！");
							} else {
								return Result.error("添加失败，设备" +
												nextDeviceExcel.getDeviceImei() + "+" + 
												nextDeviceExcel.getAddressNumber() + "重复！");
							}
						}
					}
				}
				List<DeviceArea> daList = this.deviceAreaService.list();
				for (DeviceVo deviceExcel: deviceVoList) {
					String nameProject = deviceExcel.getName();
					String[] split = nameProject.trim().split("-");
					DeviceVo deviceVo = null;
					if(split.length>1){
						deviceVo = devMapper.findDeviceCustByNamePreject(split[0],split[1]);
					}else{
						deviceVo = devMapper.findDeviceCust(nameProject).get(0);
					}


					Device device = new Device();
					device.setCustomerId(deviceVo.getId());
					
					if(StringUtils.isNotBlank(deviceExcel.getDeviceAreaName()) 
							&& daList != null && daList.size() > 0) {
						for(DeviceArea da : daList) {
							if(da.getAreaName().equalsIgnoreCase(deviceExcel.getDeviceAreaName()) 
									&& da.getCustomerId().intValue() == deviceVo.getId().intValue()) {
								device.setDeviceAreaId(da.getId());
							}
						}
					}
					
					if (deviceExcel.getDeviceType().equals("探测器")){
						device.setDeviceType("1");
					}else if (deviceExcel.getDeviceType().equals("控制器")){
						device.setDeviceType("2");
					}else if (deviceExcel.getDeviceType().equals("输出模块")){
						device.setDeviceType("3");
					}
					device.setDeviceImei(deviceExcel.getDeviceImei());
					device.setDevicePositionnum(deviceExcel.getDevicePositionnum());
					device.setAddressNumber(deviceExcel.getAddressNumber());
					device.setDeviceNum(deviceExcel.getDeviceNum());
					device.setDeviceModel(deviceExcel.getDeviceModel());
					device.setFactoryNum(deviceExcel.getFactoryNum());
					device.setDetectionTarget(deviceExcel.getDetectionTarget());
					device.setRanges(deviceExcel.getRanges());
					device.setUnit(deviceExcel.getUnit());
					device.setIccid(deviceExcel.getIccid());
					device.setReductionCoefficient(deviceExcel.getReductionCoefficient());
					device.setInstallationPosition(deviceExcel.getInstallationPosition());
					device.setExceedingAlarm(deviceExcel.getExceedingAlarm());
					device.setStatusType(deviceExcel.getStatusType());
					device.setRemark(deviceExcel.getRemark());
					deviceService.save(device);
				}
		    	String key = "allDeviceCache";
		    	this.redis.del(key);
				return  Result.ok("设备导入成功！数据行数："+deviceVoList.size());
			}catch (Exception e){
				log.error(e.getMessage(),e);
				return Result.error("抱歉! 您导入的数据中有误！");
			}finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return super.importExcel(request, response, Device.class);
	}



	// 设备表的分页查询 2019-12-24 第一版
	//@AutoLog(value = "设备-分页列表查询")
	@ApiOperation(value = "设备-分页列表查询", notes = "设备-分页列表查询")
	@GetMapping(value = "/Devicelist")
	public Result<?> queryPageList1(DeviceVo deviceVo,
									@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
									@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
//		String header = req.getHeader("X-Access-Token");
//		String username = JwtUtil.getUsername(header);
//		LoginUser sysUser = sysBaseAPI.getUserByName(username);
		QueryWrapper<DeviceVo> queryWrapper = new QueryWrapper<DeviceVo>();
		queryWrapper.orderByDesc("d.create_time");
//		queryWrapper.eq("dis.User_Id",sysUser.getId());
		super.appendDeviceAreaAuth(req, queryWrapper);
		if (deviceVo.getCustomerId() != null&&deviceVo.getCustomerId()!=0) {
			queryWrapper.eq("d.Customer_id", deviceVo.getCustomerId());
		}
		if (StrUtil.isNotBlank(deviceVo.getOneAreaName())) {
			queryWrapper.eq("c.One_area_id", deviceVo.getOneAreaName());
		}
		if (StrUtil.isNotBlank(deviceVo.getTwoAreaName())) {
			queryWrapper.eq("c.Two_area_id", deviceVo.getTwoAreaName());
		}
		if (StrUtil.isNotBlank(deviceVo.getName())) {
			queryWrapper.like("c.name", deviceVo.getName());
		}
		if (StrUtil.isNotBlank(deviceVo.getDeviceType())) {
			queryWrapper.eq("d.Device_type", deviceVo.getDeviceType());
		}
		if (StrUtil.isNotBlank(deviceVo.getDeviceImei())) {
			queryWrapper.like("d.Device_imei", "%" + deviceVo.getDeviceImei() + "%");
		}
		if (StrUtil.isNotBlank(deviceVo.getDevicePositionnum())) {
			queryWrapper.like("d.Device_positionNum", deviceVo.getDevicePositionnum());
		}
		if (StrUtil.isNotBlank(deviceVo.getDeviceNum())) {
			queryWrapper.like("d.device_num", "%" + deviceVo.getDeviceNum() + "%");
		}
		Page<DeviceVo> page = new Page<DeviceVo>(pageNo, pageSize);
		IPage<DeviceVo> pageList = deviceService.custoMpage(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * id查询设备的数量
	 *
	 * @param id
	 *
	 * @return
	 */
	//@AutoLog(value = "设备列表-id查询设备的数量")
	@ApiOperation(value = "设备列表-id查询设备的数量", notes = "设备列表-id查询设备的数量")
	@GetMapping(value = "/queryDeviceById")
	public Result<?> queryDeviceById(@RequestParam(name = "id", required = true) Integer id) {
		int count = devMapper.queryDeviceById(id);
		if (count == 0) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(count);
	}

	
	/**
	 * （通过客户id查询总设备数）
	 *
	 * @param customerId
	 * @return
	 */
	//@AutoLog(value = "前端实时数据-通过客户id查询总设备数")
	@ApiOperation(value = "前端实时数据-通过客户id查询总设备数", notes = "前端实时数据-通过客户id查询总设备数")
	@GetMapping(value = "/selectDeviceCount")
	public Result<?> selectDeviceCount(@RequestParam(name = "customerId", required = true) Integer customerId) {
		DeviceCountDTO deviceCountDTO = deviceService.selectDeviceCount(customerId);

		if (deviceCountDTO ==null) {
			return Result.error("未找到对应数据");
		}

		return Result.ok(deviceCountDTO);


	}


	/**
	 * 通过客户id,楼层id查询楼层总设备数
	 * @param customerId
	 * @param floorId
	 * @return
	 */
	//@AutoLog(value = "前端实时数据表-通过客户id查询楼层总设备数")
	@ApiOperation(value = "前端实时数据表-通过客户id查询楼层总设备数", notes = "前端实时数据表-通过客户id查询楼层总设备数")
	@GetMapping(value = "/selectCountDeviceByFlor")
	public Result<?> selectCountDeviceByFlor(@RequestParam(name = "customerId", required = true) int customerId,
											 @RequestParam(name = "floorId", required = true) int floorId) {
		DeviceCountDTO deviceCountDTO = deviceService.selectCountDeviceByFlor(customerId, floorId);
		if (deviceCountDTO ==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(deviceCountDTO);
	}


//todo 001
	/**
	 * 根据用户id查询设备情况
	 * @param uid
	 * @return
	 */
	@GetMapping("queryDeviceCountByUid")
	@ApiOperation(value = "根据用户id查询设备情况 ", notes = "根据用户id查询设备情况 ")
	public Result<?> queryDeviceCountByUid(@RequestParam("uid") String uid) {
		DeviceCountDTO deviceCountDTO=	deviceService.queryDeviceCountByUid(uid);

		return Result.ok(deviceCountDTO);
	}


//todo 002
	/**
	 * 根据客户id查询设备类型数量
	 * @param cid
	 * @return
	 */
	@GetMapping("queryDeviceTypeCountByCid")
	@ApiOperation(value = "根据客户id查询设备类型数量 ", notes = "根据客户id查询设备类型数量 ")
	public Result<?> queryDeviceTypeCountByCid(@RequestParam("cid") Integer cid,
			HttpServletRequest req) {

		DeviceTypeCount deviceTypeCount=deviceService.queryDeviceTypeCountByCid(cid,
				super.getLoginUser(req).getId());

		return Result.ok(deviceTypeCount);
	}

	//todo 003
	/**
	 * 根据用户id查询设备类型数量
	 * @param uid
	 * @return
	 */
	@GetMapping("queryDeviceTypeCountByUid")
	@ApiOperation(value = "根据用户id查询设备类型数量 ", notes = "根据用户id查询设备类型数量 ")
	public Result<?> queryDeviceTypeCountByUid(@RequestParam("uid") String uid) {
		DeviceTypeCount deviceTypeCount=deviceService.queryDeviceTypeCountByUid(uid);
		return Result.ok(deviceTypeCount);
	}

}
