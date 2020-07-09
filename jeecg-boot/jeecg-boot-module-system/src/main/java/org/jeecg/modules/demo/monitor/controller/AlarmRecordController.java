package org.jeecg.modules.demo.monitor.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.mapper.AlarmNumVoMapper;
import org.jeecg.modules.demo.monitor.mapper.AlarmRecordVoMapper;
import org.jeecg.modules.demo.monitor.mapper.DistributionMapper;
import org.jeecg.modules.demo.monitor.service.IAlarmRecordService;
import org.jeecg.modules.demo.monitor.service.IAlarmRecordVoService;
import org.jeecg.modules.demo.monitor.vo.AlarmNumVo;
import org.jeecg.modules.demo.monitor.vo.AlarmNumVo2;
import org.jeecg.modules.demo.monitor.vo.AlarmRecordVo;
import org.jeecg.modules.demo.monitor.vo.AlarmServenVO;
import org.jeecg.modules.demo.monitor.vo.AlarmWindowVo;
import org.jeecg.modules.demo.monitor.vo.DeviceTypeCount;
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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 报警记录表
 * @Author: jeecg-boot
 * @Date: 2019-12-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "报警记录表")
@RestController
@RequestMapping("/monitor/alarmRecord")
public class AlarmRecordController extends JeecgController<AlarmRecord, IAlarmRecordService> {
	@Autowired
	private IAlarmRecordService alarmRecordService;
	@Autowired
	private IAlarmRecordVoService iAlarmRecordVoService;
	@Autowired
	private AlarmRecordVoMapper alMapper;
	@Autowired
	private AlarmNumVoMapper alarmNumVoService;
	@Autowired
	private DistributionMapper distributionMapper;

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
	 * 报警分页列联合查询表
	 *
	 * @param alarmRecordVo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "前端报警记录联合查询表-分页列表查询")
	@ApiOperation(value = "报警记录联合查询表-分页列表查询", notes = "报警记录联合查询表-分页列表查询")
	@GetMapping(value = "/queryAlarm")
	public Result<?> queryAlarm(AlarmRecordVo alarmRecordVo,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<AlarmRecordVo> queryWrapper = new QueryWrapper<>();
		queryWrapper.groupBy("a.id");
		queryWrapper.orderByDesc("a.Alarm_time desc,id");
		//根据用户id查询
//		if(alarmRecordVo.getUserId()!= null&&alarmRecordVo.getUserId()!=""){
////			queryWrapper.eq("dis.User_id", alarmRecordVo.getUserId());
//			super.appendDeviceAreaAuth(req, queryWrapper);
//		}
		super.appendDeviceAreaAuth(req, queryWrapper);
		//根据客户id查询报警记录
		if (alarmRecordVo.getCustomerId() != null) {
			queryWrapper.eq("d.Customer_id", alarmRecordVo.getCustomerId());
			if(alarmRecordVo.getUserId()== null||("".equals(alarmRecordVo.getUserId()))){
				String userId = distributionMapper.queryBycid(alarmRecordVo.getCustomerId());
				alarmRecordVo.setUserId(userId);
				queryWrapper.eq("dis.User_id", alarmRecordVo.getUserId());
			}
		}

		// 一二级区域查询
		if (alarmRecordVo.getOneAreaId() != null) {
			queryWrapper.eq("c.One_area_id", alarmRecordVo.getOneAreaId());
		}
		if (alarmRecordVo.getTwoAreaId() != null) {
			queryWrapper.eq("c.Two_area_id", alarmRecordVo.getTwoAreaId());
		}
//		//楼层图查询
//		if (alarmRecordVo.getFloorName() != null&&alarmRecordVo.getFloorName() != "") {
//			queryWrapper.eq("f.Floor_name", alarmRecordVo.getFloorName());
//		}
		//设备类型
		if (alarmRecordVo.getAlarmDeviceType() != null&&alarmRecordVo.getAlarmDeviceType() != "") {
			if(NumberUtils.isNumber(alarmRecordVo.getAlarmDeviceType())){
				queryWrapper.like("d.Device_type", alarmRecordVo.getAlarmDeviceType());
			}else{
				queryWrapper.like("d.Device_type", map1.get(alarmRecordVo.getAlarmDeviceType()));
			}
		}
		// 时间判断
		if (StrUtil.isNotBlank(req.getParameter("alarmTime_begin"))) {
			if (StrUtil.isNotBlank(req.getParameter("alarmTime_end"))) {
				queryWrapper.between("Alarm_time", req.getParameter("alarmTime_begin"),
						req.getParameter("alarmTime_end"));
			} else {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				queryWrapper.between("Alarm_time", req.getParameter("alarmTime_begin"), df.format(new Date()));
			}
		} else {
			if (StrUtil.isNotBlank(req.getParameter("alarmTime_end"))) {
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.YEAR, -200);
				Date y = c.getTime();
				queryWrapper.between("Alarm_time", y, req.getParameter("alarmTime_end"));
			}
		}
		String testDeviceImei;
		String sbdz;
		if(StringUtils.isNotEmpty(testDeviceImei = req.getParameter("testDeviceImei")) && 
			StringUtils.isNotEmpty(sbdz = req.getParameter("sbdz")) ) {
			queryWrapper.eq("Test_deviceImei", testDeviceImei);
			queryWrapper.eq("Test_address", sbdz);
		}
		
		// 设备编号
		if(StringUtils.isNotEmpty(alarmRecordVo.getDeviceNum())) {
			queryWrapper.like("d.Device_num", "%" + alarmRecordVo.getDeviceNum() + "%");
		}
		
		Page<AlarmRecordVo> page = new Page<>(pageNo, pageSize);
		IPage<AlarmRecordVo> iPage = iAlarmRecordVoService.selectAlarmAll(page, queryWrapper);
		List<String> list = null;
		for (int i = 0; i < iPage.getRecords().size(); i++) {
			AlarmRecordVo aVo = iPage.getRecords().get(i);
			if (aVo != null) {
				String str = aVo.getTstatus();
				if(StringUtils.isNotBlank(str)) {
//					String status = aVo.getTstatus();
					list = Arrays.asList(str.split(","));
					for (int j = 0; j < list.size(); j++) {
//						if (list.get(j).indexOf("高警")>0) {
						iPage.getRecords().get(i).setTstatus(map1.get(list.get(j)));
//						}else if (list.get(j).indexOf("低警")>0) {
//							iPage.getRecords().get(i).setTstatus("低警");
//						}
					}
				}

			}
		}

//		System.out.println(iPage.getRecords());
		return Result.ok(iPage);
	}

	/**
	 * 报警分页列联合查询表
	 *
	 * @param alarmRecordVo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "后台报警记录联合查询表-分页列表查询")
	@ApiOperation(value = "报警记录联合查询表-分页列表查询", notes = "报警记录联合查询表-分页列表查询")
	@GetMapping(value = "/queryAlarmVo")
	public Result<?> queryAlarmVo(AlarmRecordVo alarmRecordVo,
								@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<AlarmRecordVo> queryWrapper = new QueryWrapper<AlarmRecordVo>().orderByDesc("a.Alarm_time");
		//根据客户id查询报警记录
		if (alarmRecordVo.getCustomerId() != null) {
			queryWrapper.eq("d.Customer_id", alarmRecordVo.getCustomerId());
		}
		// 一二级区域查询
		if (alarmRecordVo.getOneAreaName() != null) {
			queryWrapper.eq("c.One_area_id", alarmRecordVo.getOneAreaName());
		}
		if (alarmRecordVo.getTwoAreaName() != null) {
			queryWrapper.eq("c.Two_area_id", alarmRecordVo.getTwoAreaName());
		}
		//楼层图查询
		if (alarmRecordVo.getFloorName() != null) {
			queryWrapper.eq("f.Id", alarmRecordVo.getFloorName());
		}
		// 客户名称查询
		if (alarmRecordVo.getName() != null) {
			queryWrapper.eq("d.Customer_id", alarmRecordVo.getName());
		}
		// 设备编号模糊查询
		if (StrUtil.isNotBlank(alarmRecordVo.getDeviceNum())) {
			queryWrapper.like("d.Device_num", alarmRecordVo.getDeviceNum());
		}
		// 设备IMEI
		if (StrUtil.isNotBlank(alarmRecordVo.getAlarmDeviceImei())) {
			queryWrapper.like("a.Alarm_deviceImei", alarmRecordVo.getAlarmDeviceImei());
		}
		//报警类型
		if (StrUtil.isNotBlank(alarmRecordVo.getTstatus())) {
			queryWrapper.like("a.T_status", alarmRecordVo.getTstatus());
		}
		//设备类型查询
		if (alarmRecordVo.getAlarmDeviceType() != null) {
			queryWrapper.eq("d.Device_type", alarmRecordVo.getAlarmDeviceType());
		}
		// 时间判断
		if (StrUtil.isNotBlank(req.getParameter("alarmTime_begin"))) {
			if (StrUtil.isNotBlank(req.getParameter("alarmTime_end"))) {
				queryWrapper.between("Alarm_time", req.getParameter("alarmTime_begin"),
						req.getParameter("alarmTime_end"));
			} else {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				queryWrapper.between("Alarm_time", req.getParameter("alarmTime_begin"), df.format(new Date()));
			}
		} else {
			if (StrUtil.isNotBlank(req.getParameter("alarmTime_end"))) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.YEAR, -200);
				Date y = c.getTime();
				queryWrapper.between("Alarm_time", y, req.getParameter("alarmTime_end"));
			}
		}
		super.appendDeviceAreaAuth(req, queryWrapper);
		Page<AlarmRecordVo> page = new Page<AlarmRecordVo>(pageNo, pageSize);
		IPage<AlarmRecordVo> iPage = iAlarmRecordVoService.selectAlarmVoAll(page, queryWrapper);


		return Result.ok(iPage);
	}


	/**
	 * 分页列表查询
	 *
	 * @param alarmRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "报警记录表-分页列表查询")
	@ApiOperation(value = "报警记录表-分页列表查询", notes = "报警记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(AlarmRecord alarmRecord,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<AlarmRecord> queryWrapper = QueryGenerator.initQueryWrapper(alarmRecord, req.getParameterMap());
		Page<AlarmRecord> page = new Page<AlarmRecord>(pageNo, pageSize);
		IPage<AlarmRecord> pageList = alarmRecordService.page(page, queryWrapper);
		return Result.ok(pageList);
	}


	//@AutoLog(value = "报警记录表-清空")
	@ApiOperation(value = "报警记录表-清空", notes = "报警记录表-清空")
	@GetMapping(value = "/deleteAll")
	public Result<?> deleteAll() {
		alarmRecordService.deleteAll();
		return Result.ok("已清空");
	}

	/**
	 * 添加
	 *
	 * @param alarmRecord
	 * @return
	 */
	//@AutoLog(value = "报警记录表-添加")
	@ApiOperation(value = "报警记录表-添加", notes = "报警记录表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody AlarmRecord alarmRecord) {
		alarmRecordService.save(alarmRecord);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param alarmRecord
	 * @return
	 */
	//@AutoLog(value = "报警记录表-编辑")
	@ApiOperation(value = "报警记录表-编辑", notes = "报警记录表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody AlarmRecord alarmRecord) {
		alarmRecordService.updateById(alarmRecord);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "报警记录表-通过id删除")
	@ApiOperation(value = "报警记录表-通过id删除", notes = "报警记录表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		alarmRecordService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	//@AutoLog(value = "报警记录表-批量删除")
	@ApiOperation(value = "报警记录表-批量删除", notes = "报警记录表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.alarmRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "报警记录表-通过id查询")
	@ApiOperation(value = "报警记录表-通过id查询", notes = "报警记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		AlarmRecord alarmRecord = alarmRecordService.getById(id);
		return Result.ok(alarmRecord);
	}

//	/**
//	 * 导出excel
//	 *
//	 * @param request
//	 * @param alarmRecord
//	 */
//	@RequestMapping(value = "/exportXls")
//	public ModelAndView exportXls(HttpServletRequest request, AlarmRecordVo alarmRecord) {
//		return super.exportXls(request, alarmRecord, AlarmRecordVo.class, "报警记录表");
//	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param alarmRecordVo
	 */
	//@AutoLog(value = "报警记录联合查询表-Excel导出")
	@ApiOperation(value = "报警记录联合查询表-Excel导出", notes = "报警记录联合查询表-Excel导出")
	@GetMapping(value = "/exportXls")
	public Result<List<AlarmRecordVo>> exportXls(HttpServletRequest request, 
			AlarmRecordVo alarmRecordVo,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
		// Step.1 组装查询条件
		QueryWrapper<AlarmRecordVo> queryWrapper = new QueryWrapper<AlarmRecordVo>().orderByDesc("a.Alarm_time desc,a.id");
//		queryWrapper.groupBy("a.id");
		Result<List<AlarmRecordVo>> result = new Result<List<AlarmRecordVo>>();
		//用户id查询
		if (alarmRecordVo.getUserId() != null&&!("".equals(alarmRecordVo.getUserId()))){
//			queryWrapper.eq("dis.User_id", alarmRecordVo.getUserId());
			super.appendDeviceAreaAuth(request, queryWrapper);
		}
		// 根据客户id查询报警记录
		if (alarmRecordVo.getCustomerId() != null) {
			queryWrapper.eq("d.Customer_id", alarmRecordVo.getCustomerId());
			if(alarmRecordVo.getUserId()== null||("".equals(alarmRecordVo.getUserId()))){
				String userId = distributionMapper.queryBycid(alarmRecordVo.getCustomerId());
				alarmRecordVo.setUserId(userId);
				queryWrapper.eq("dis.User_id", alarmRecordVo.getUserId());
			}
		}
		// 一二级区域查询
		if (alarmRecordVo.getOneAreaId() != null) {
			queryWrapper.eq("c.One_area_id", alarmRecordVo.getOneAreaId());
		}
		if (alarmRecordVo.getTwoAreaId() != null) {
			queryWrapper.eq("c.Two_area_id", alarmRecordVo.getTwoAreaId());
		}
		// 楼层图查询
		if (alarmRecordVo.getFloorName() != null&&!("".equals(alarmRecordVo.getFloorName()))) {
			queryWrapper.eq("f.Floor_name", alarmRecordVo.getFloorName());
		}
		// 设备类型
		if (alarmRecordVo.getAlarmDeviceType() != null&&!("".equals(alarmRecordVo.getAlarmDeviceType()))) {
			if(NumberUtils.isNumber(alarmRecordVo.getAlarmDeviceType())){
				queryWrapper.like("d.Device_type", alarmRecordVo.getAlarmDeviceType());
			}else{
				queryWrapper.like("d.Device_type", map1.get(alarmRecordVo.getAlarmDeviceType()));
			}
		}
		// 时间判断
		if (StrUtil.isNotBlank(request.getParameter("alarmTime_begin"))) {
			if (StrUtil.isNotBlank(request.getParameter("alarmTime_end"))) {
				queryWrapper.between("Alarm_time", request.getParameter("alarmTime_begin"),
						request.getParameter("alarmTime_end"));
			} else {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				queryWrapper.between("Alarm_time", request.getParameter("alarmTime_begin"), df.format(new Date()));
			}
		} else {
			if (StrUtil.isNotBlank(request.getParameter("alarmTime_end"))) {
				queryWrapper.le("Alarm_time", request.getParameter("alarmTime_end"));
			}
		}
		// 设备编号
		if(StringUtils.isNotEmpty(alarmRecordVo.getDeviceNum())) {
			queryWrapper.like("d.Device_num", "%" + alarmRecordVo.getDeviceNum() + "%");
		}
		// Step.2 获取导出数据
//		queryWrapper.last(" limit " + ((pageNo - 1) * pageSize)  + "," + pageSize + " ");
		List<AlarmRecordVo> pageList = iAlarmRecordVoService.exportXlsAlarmAll(queryWrapper);
		List<String> list = null;
		for (int i = 0; i < pageList.size(); i++) {
			AlarmRecordVo aVo = pageList.get(i);
			if (aVo != null) {
				if(StringUtils.isNotEmpty(aVo.getDeviceNum()) 
						&& aVo.getDeviceNum().endsWith("/")
						) {
					aVo.setDeviceNum(aVo.getDeviceNum().substring(0, aVo.getDeviceNum().length() - 1));
				}
				String tstatus = aVo.getTstatus();
				String alarmState = aVo.getAlarmState();
				String alarmDeviceType = aVo.getAlarmDeviceType();
				//判断报警状态
				if (alarmState.equals("0")) {
					aVo.setAlarmState("触发报警");
				}else if (alarmState.equals("1")) {
					aVo.setAlarmState("解除报警");
				}
				//判断设备类型
				if (alarmDeviceType.equals("1")) {
					aVo.setAlarmDeviceType("探测器");
				}else if (alarmDeviceType.equals("2")) {
					aVo.setAlarmDeviceType("控制器");
				}else if (alarmDeviceType.equals("3")) {
					aVo.setAlarmDeviceType("输出模块");
				}
				//判断报警类型
				list = Arrays.asList(tstatus.split(","));
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).equals("高警")) {
						aVo.setTstatus("高警");
					}else if (list.get(j).equals("低警")) {
						aVo.setTstatus("低警");
					}
				}
			}
		}
		result.setMessage("操作成功!");
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * 根据id查询实时报警情况窗口
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "报警列表-根据id查询实时报警情况窗口")
	@ApiOperation(value = "报警列表-根据id查询实时报警情况窗口", notes = "报警列表-根据id查询实时报警情况窗口")
	@GetMapping(value = "/queryAlarmRecordById")
	public Result<?> queryAlarmRecordById(@RequestParam(name = "id", required = true) Integer id) {
		List<AlarmWindowVo> alarmWindowVo = alMapper.queryAlarmRecordById(id);
		if (alarmWindowVo == null || alarmWindowVo.size() < 0) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(alarmWindowVo);
	}

	//@AutoLog(value = "通过用户/客户查询客户的最近7天每一天的设备报警次数")
	@ApiOperation(value = "通过用户/客户查询客户的最近7天每一天的设备报警次数", notes = "通过用户/客户查询客户的最近7天每一天的设备报警次数")
	@RequestMapping(value = "/selectSevenDayAll",method = RequestMethod.GET)
	public Result<Map<List<AlarmNumVo>,String>> selectSevenDayAll(AlarmServenVO alarmServenVO){
		Result<Map<List<AlarmNumVo>,String>> result = new Result<>();
		if("".equals(alarmServenVO.getUserId())){
			alarmServenVO.setUserId(null);
		}
		if(alarmServenVO.getUserId()==null){
			if(alarmServenVO.getCustomerId()!=null&&("".equals(alarmServenVO.getCustomerId()))){
				String userId = distributionMapper.queryBycid(alarmServenVO.getCustomerId());
				alarmServenVO.setUserId(userId);
			}
		}
		if(("".equals(alarmServenVO.getFloorName())||alarmServenVO.getFloorName()==null)){
			alarmServenVO.setFloorName(null);
		}else{
			alarmServenVO.setFloorName("%"+alarmServenVO.getFloorName()+"%");
		}
		if(("".equals(alarmServenVO.getDeviceNum()))||alarmServenVO.getDeviceNum()==null){
			alarmServenVO.setDeviceNum(null);
		}else{
			alarmServenVO.setDeviceNum("%"+alarmServenVO.getDeviceNum()+"%");
		}
//		List<AlarmNumVo> list  = alarmNumVoService.selectSevenDayAll(alarmServenVO);
		if(alarmServenVO.getAlarmDeviceType()==null||alarmServenVO.getAlarmDeviceType()==""){
			alarmServenVO.setAlarmDeviceType("1");
			List<AlarmNumVo> list1 = alarmNumVoService.selectSevenDayAll(alarmServenVO);
			alarmServenVO.setAlarmDeviceType("2");
			List<AlarmNumVo> list2 = alarmNumVoService.selectSevenDayAll(alarmServenVO);
			alarmServenVO.setAlarmDeviceType("3");
			List<AlarmNumVo> list3 = alarmNumVoService.selectSevenDayAll(alarmServenVO);
			Map map =  new HashedMap();
			map.put("type1", list1);
			map.put("type2", list2);
			map.put("type3", list3);
//		map.put("type",list);
			result.setResult(map);
			if (map != null && map.size() != 0) {
				result.setResult(map);
				result.setSuccess(true);
			}else {
				result.error500("list为空");
			}
		}else{
			String typed = map1.get(alarmServenVO.getAlarmDeviceType());
			alarmServenVO.setAlarmDeviceType(typed);
			List<AlarmNumVo> list = alarmNumVoService.selectSevenDayAll(alarmServenVO);
			Map map =  new HashedMap();
			map.put("type"+typed, list);
			result.setResult(map);
			if (map != null && map.size() != 0) {
				result.setResult(map);
				result.setSuccess(true);
			}else {
				result.error500("list为空");
			}
		}
//		List<AlarmNumVo> list1 = alarmNumVoService.selectSevenDayAll1(alarmServenVO.getCustomerId().toString());
//		List<AlarmNumVo> list2 = alarmNumVoService.selectSevenDayAll2(alarmServenVO.getCustomerId().toString());
//		List<AlarmNumVo> list3 = alarmNumVoService.selectSevenDayAll3(alarmServenVO.getCustomerId().toString());


		return result;
	}

	//@AutoLog(value = "通过用户/客户查询客户的最近1天内每小时的设备报警次数TOP10")
	@ApiOperation(value = "通过用户/客户查询客户的最近1天内每小时的设备报警次数TOP10", notes = "通过用户/客户查询客户的最近1天内每小时的设备报警次数TOP10")
	@RequestMapping(value = "/selectOneDayAll",method = RequestMethod.GET)
	public Result<Map<List<AlarmNumVo>,String>> selectOneDayAll(AlarmServenVO alarmServenVO){
		Result<Map<List<AlarmNumVo>,String>> result = new Result<Map<List<AlarmNumVo>,String>>();
//		List<AlarmNumVo2> list = alarmNumVoService.selectOneDayTop10(alarmServenVO.getCustomerId().toString());
		if(alarmServenVO.getDeviceNum()!=null&&!("".equals(alarmServenVO.getDeviceNum()))){
			alarmServenVO.setDeviceNum("%"+alarmServenVO.getDeviceNum()+"%");
		}
		if("".equals(alarmServenVO.getUserId())){
			alarmServenVO.setUserId(null);
		}
		if(alarmServenVO.getUserId()==null){
			if(alarmServenVO.getCustomerId()!=null&&("".equals(alarmServenVO.getCustomerId()))){
				String userId = distributionMapper.queryBycid(alarmServenVO.getCustomerId());
				alarmServenVO.setUserId(userId);
			}
		}
		List<AlarmNumVo2>  list2 = alarmNumVoService.selectOneTop10(alarmServenVO);

		Map map =  new HashedMap();
		if (list2 != null && list2.size() != 0) {
			for(int i=0;i<list2.size();i++) {
				HashMap<String, List<AlarmNumVo>> stringListHashMap = new HashMap<>();
				AlarmNumVo2 alarmNumVo2 = list2.get(i);
				List<AlarmNumVo> list1 = alarmNumVoService.selectOneDay(alarmNumVo2.getDeviceId());
				stringListHashMap.put(map1.get(alarmNumVo2.getAlarmDeviceType())+alarmNumVo2.getDeviceNum(),list1);
				map.put(map1.get(alarmNumVo2.getAlarmDeviceType())+alarmNumVo2.getDeviceNum(), list1);
			}
			result.setResult(map);
			result.setSuccess(true);
		}else {
			result.error500("list为空");
		}
		return result;
	}

	//@AutoLog(value = "通过设备imei和设备地址编号查询当前设备7天每一天的报警次数")
	@ApiOperation(value = "通过设备imei和设备地址编号查询当前设备7天每一天的报警次数", notes = "通过设备imei和设备地址编号查询当前设备7天每一天的报警次数")
	@RequestMapping(value = "/selectSevenDayOne",method = RequestMethod.GET)
	public Result<List<AlarmNumVo>> selectSevenDayOne(@RequestParam(name = "deviceId",required = true)Integer deviceId){
		Result<List<AlarmNumVo>> result = new Result<List<AlarmNumVo>>();
		List<AlarmNumVo> list = alarmNumVoService.selectSevenDayOne(deviceId);
		if (list != null && list.size() != 0) {
			result.setResult(list);
			result.setSuccess(true);
		}else {
			result.error500("list为空");
		}
		return result;
	}

	//@AutoLog(value = "通过设备imei和设备地址编号查询当前设备1天没每小时的报警次数")
	@ApiOperation(value = "通过设备imei和设备地址编号查询当前设备1天没每小时的报警次数", notes = "通过设备imei和设备地址编号查询当前设备1天没每小时的报警次数")
	@RequestMapping(value = "/selectOneDay",method = RequestMethod.GET)
	public Result<List<AlarmNumVo>> selectOneDay(@RequestParam(name = "deviceId",required = true)Integer deviceId){
		Result<List<AlarmNumVo>> result = new Result<List<AlarmNumVo>>();
		List<AlarmNumVo> list = alarmNumVoService.selectOneDay(deviceId);
		if (list != null && list.size() != 0) {
			result.setResult(list);
			result.setSuccess(true);
		}else {
			result.error500("list为空");
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
		return super.importExcel(request, response, AlarmRecord.class);
	}


	//todo 2020 01 12 001改动 之设备情况


//todo 004
	/**
	 * 根据客户id查询设备类型及所属的警报数量
	 * @param cid
	 * @return
	 */
	@GetMapping("queryAlarmCountByCid")
	@ApiOperation(value = "根据客户id查询设备类型及所属的警报数量", notes = "根据客户id查询设备类型及所属的警报数量")
	public Result<?> queryAlarmCountByCid(@RequestParam("cid") Integer cid,
			HttpServletRequest req) {

	DeviceTypeCount deviceTypeCount= alarmRecordService.queryAlarmCountByCidnew(cid,
			super.getLoginUser(req).getId());

		return Result.ok(deviceTypeCount);
	}

	//todo 005
	/**
	 * 根据用户id查询设备类型及所属的警报数量
	 * @param uid
	 * @return
	 */
	@GetMapping("queryAlarmCountByUid")
	@ApiOperation(value = "根据用户id查询设备类型及所属的警报数量", notes = "根据用户id查询设备类型及所属的警报数量")
	public Result<?> queryAlarmCountByUid(@RequestParam("uid") String uid) {
		DeviceTypeCount deviceTypeCount= alarmRecordService.queryAlarmCountByUid(uid);
		return Result.ok(deviceTypeCount);
	}


	/**
	 * 根据id查询用户登录所有客户实时报警情况窗口
	 * @param uid
	 * @return
	 */
	//@AutoLog(value = "报警列表-根据id查询用户登录所有客户实时报警情况窗口")
	@ApiOperation(value = "报警列表-根据id查询用户登录所有客户实时报警情况窗口", notes = "报警列表-根据id查询用户登录所有客户实时报警情况窗口")
	@GetMapping(value = "/queryAlarmRecordByUid")
	public Result<?> queryAlarmRecordByUid(@RequestParam(name = "uid", required = true) String uid ) {
		List<AlarmWindowVo> alarmWindowVo = alMapper.queryAlarmRecordByUid(uid);

		if (alarmWindowVo == null || alarmWindowVo.size() < 0) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(alarmWindowVo);
	}
	/**
	  *   实时报警弹窗
	 * @param alarmRecordVo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "实时报警弹窗")
	@ApiOperation(value = "实时报警弹窗", notes = "实时报警弹窗")
	@GetMapping(value = "/toDayAlarm")
	public Object toDayAlarm(
			HttpServletRequest req,
			@RequestParam(name = "userId", required = true) String uid,
			@RequestParam(required = false) Integer limit) {
		QueryWrapper<AlarmRecordVo> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("a.Alarm_time DESC,a.id");
		Page<AlarmRecordVo> page = new Page<>(1, Integer.MAX_VALUE);
		if(limit == null) { // 右下角报警弹窗
			// 时间判断
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			queryWrapper.between("Alarm_time", 
//					df.format(new Date()) + " 00:00:00", 
//					df.format(new Date()) + " 23:59:59");
			queryWrapper.notLike("d.status_type", "维护");
		}  else { // 首页实时报警
			queryWrapper.like("d.status_type", "警");
			page = new Page<>(1, limit);
		}
//		QueryWrapper<Distribution> disWrapper = new QueryWrapper<Distribution>();
//		disWrapper.eq("User_Id", uid);
//		List<Distribution> disList = distributionMapper.selectList(disWrapper);
//		if(disList != null) {
//			Set<String> customerIds = new HashSet<String>();
//			for(Distribution dis : disList) {
//				customerIds.add(dis.getCustomerId());
//			}
//			queryWrapper.in("D.customer_id", customerIds);
//		}
		super.appendDeviceAreaAuth(req, queryWrapper);
		queryWrapper.apply("d.status_type != '正常' and a.id is not null ");
//		queryWrapper.apply("d.status_type != '正常' and a.Alarm_time is not null ");
//		IPage<AlarmRecordVo> iPage = iAlarmRecordVoService.selectAlarmAll(page, queryWrapper);
		IPage<AlarmRecordVo> iPage = iAlarmRecordVoService.selectDeviceRealAlarm(page, queryWrapper);
		List<String> list = null;
		for (int i = 0; i < iPage.getRecords().size(); i++) {
			AlarmRecordVo aVo = iPage.getRecords().get(i);
			if (aVo != null) {
				String str = aVo.getTstatus();
				list = Arrays.asList(str.split(","));
				for (int j = 0; j < list.size(); j++) {
					iPage.getRecords().get(i).setTstatus(map1.get(list.get(j)));
				}
			}
		}
		return JSON.toJSON(iPage);
	}

}
