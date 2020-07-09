package org.jeecg.modules.system.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.MD5Util;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.encryption.EncryptedString;
import org.jeecg.modules.demo.monitor.entity.CustomerAccounts;
import org.jeecg.modules.demo.monitor.entity.Customers;
import org.jeecg.modules.demo.monitor.service.IAreaService;
import org.jeecg.modules.demo.monitor.service.ICustomerAccountsService;
import org.jeecg.modules.demo.monitor.service.ICustomersService;
import org.jeecg.modules.shiro.vo.DefContants;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.SysLoginModel;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysLogService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.util.SendShortMessageUtil;
import org.jeecg.modules.system.vo.PhoneCodeVO;
import org.jeecg.modules.system.vo.UserVO;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

import cn.hutool.core.util.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags="用户登录")
@Slf4j
public class LoginController {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	private ISysLogService logService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private ICustomerAccountsService customerAccountsService;
	@Autowired
	private ICustomersService customersService;
	@Autowired
	private IAreaService areaService;

	private static final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

	@ApiOperation("客户登录接口")
	@RequestMapping(value = "/login1", method = RequestMethod.POST)
	public Result<JSONObject> login1(@RequestBody SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		Object checkCode = redisUtil.get(sysLoginModel.getCheckKey());
		if(checkCode==null) {
			result.error500("验证码失效");
			return result;
		}
		if(!checkCode.toString().toLowerCase().equals(sysLoginModel.getCaptcha().toLowerCase())) {
			result.error500("验证码错误");
			return result;
		}
		//update-end-author:taoyan date:20190828 for:校验验证码
		//1. 校验用户是否有效
		CustomerAccounts customerAccount = customerAccountsService.getUserByName(username);
		result = customerAccountsService.checkUserIsEffective(customerAccount);
		if(!result.isSuccess()) {
			return result;
		}

		//2. 校验用户名或密码是否正确
//		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
//		String syspassword = sysUser.getPassword();
		String customerpassword = customerAccount.getPassword();
		if (!customerpassword.equals(password)) {
			result.error500("用户名或密码错误");
			return result;
		}

		//用户登录信息
//		userInfo(cu, result);
		costomerInfo(customerAccount,result);
		sysBaseAPI.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}
	@ApiOperation("用户重置密码接口")
	@RequestMapping(value = "/reversUserPassword", method = RequestMethod.POST)
	public Result<?> reversUserPassword(@RequestBody UserVO userVO) {
		SysUser user = sysUserService.getById(userVO.getId());//获得用户对象
		//密码对比开始
		String userpassword = PasswordUtil.encrypt(user.getUsername(), userVO.getOldPassword(), user.getSalt());
		String syspassword = user.getPassword();
		if (!syspassword.equals(userpassword)) {
//			result.error500("用户名或密码错误");
			return Result.error("用户名或密码错误");
		}
		//密码正确，进行密码修改
		String encryptPassword = PasswordUtil.encrypt(user.getUsername(), userVO.getNewPassword(), user.getSalt());
		user.setPassword(encryptPassword);
		boolean b = sysUserService.updateById(user);
		if(!b){
			return Result.error("密码修改失败");
		}
		return Result.ok("密码修改成功");
	}
	@ApiOperation("客户重置密码接口")
	@RequestMapping(value = "/reversCustomerPassword", method = RequestMethod.POST)
	public Result<?> reversCustomerPassword(@RequestBody UserVO userVO) {
		CustomerAccounts customerAccounts = customerAccountsService.getCustomerId(userVO.getId());//获得客户对象
		//密码对比开始
		String userpassword = userVO.getOldPassword();
		String syspassword = customerAccounts.getPassword();
		if (!syspassword.equals(userpassword)) {
//			result.error500("用户名或密码错误");
			return Result.error("用户名或密码错误");
		}
		//密码正确，进行密码修改
		String encryptPassword = userVO.getNewPassword();
		customerAccounts.setPassword(encryptPassword);
//		boolean b = sysUserService.updateById(user);
		boolean b = customerAccountsService.updateById(customerAccounts);
		if(!b){
			return Result.error("密码修改失败");
		}

		return Result.ok("密码修改成功");
	}

	@ApiOperation("用户登录接口")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		if("4G".equals(sysLoginModel.getSystemDataType())) {
			Object checkCode = redisUtil.get(sysLoginModel.getCheckKey());
			if(checkCode==null) {
				result.error500("验证码失效");
				return result;
			}
			if(!checkCode.toString().toLowerCase().equals(sysLoginModel.getCaptcha().toLowerCase())) {
				result.error500("验证码错误");
				return result;
			}
		}
		//update-end-author:taoyan date:20190828 for:校验验证码
		//1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}
		//用户登录信息
		userInfo(sysUser, result);
		sysBaseAPI.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	@ApiOperation("客户找回密码接口")
	@RequestMapping(value = "/retrievePassword1", method = RequestMethod.POST)
	public Result<JSONObject> retrievePassword1(@RequestBody PhoneCodeVO customer){
		PhoneCodeVO phoneCodeVO = new PhoneCodeVO();
		Result<JSONObject> result = new Result<JSONObject>();
		System.out.println(customer);
		String username = customer.getUsername();
		String password = customer.getPassword();
//		String encryptPassword = PasswordUtil.encrypt(username, password, "1234");
		CustomerAccounts customerAccounts = customerAccountsService.getUserByName(username);
		//校验用户是否有效
		if(customerAccounts==null){
			result.error500("用户名错误");
			return result;
		}
		String checkCode = (String)redisUtil.get(customerAccounts.getPhone());
		System.out.println("验证码："+checkCode);
		//获得用户名，密码，验证码
		if(checkCode==null) {
			result.error500("验证码失效");
			return result;
		}
		if(!checkCode.equals(customer.getCode())) {
			result.error500("验证码错误");
			return result;
		}
		customerAccounts.setPassword(password);
		boolean b = customerAccountsService.updateById(customerAccounts);
//		boolean b = customerAccountsService.savePassword(username, password);
		if(!b){
			result.error500("找回密码失败，请稍后重试");
			return result;
		}
		result.success("密码修改成功");
		return result;
	}

	@ApiOperation("用户找回密码接口")
	@RequestMapping(value = "/retrievePassword", method = RequestMethod.POST)
	public Result<JSONObject> retrievePassword(@RequestBody PhoneCodeVO sysmodal){
		Result<JSONObject> result = new Result<JSONObject>();
		System.out.println(sysmodal);
		String username = sysmodal.getUsername();
		String password = sysmodal.getPassword();

		SysUser userByName = sysUserService.getUserByName(username);
		if(userByName==null){
			result.error500("用户名错误");
			return result;
		}
		Object checkCode = redisUtil.get(userByName.getPhone());
		//获得用户名，密码，验证码
		System.out.println("验证码："+checkCode);
		if(checkCode==null) {
			result.error500("验证码失效");
			return result;
		}
		if(!checkCode.equals(sysmodal.getCode())) {
			result.error500("验证码错误");
			return result;
		}

		String salt = oConvertUtils.randomGen(8);
		userByName.setSalt(salt);
		String encryptPassword = PasswordUtil.encrypt(username, password, userByName.getSalt());
		userByName.setPassword(encryptPassword);
		boolean b = sysUserService.updateById(userByName);
		if(!b){
			result.error500("找回密码失败，请稍后重试");
			return result;
		}
		result.success("密码重置成功");
		return result;
	}

	@ApiOperation("用户短信验证码接口")
	@RequestMapping(value = "/shortMessage", method = RequestMethod.POST)
	public Result<JSONObject> shortMessage(@RequestParam("username") String username){
		Result<JSONObject> result = new Result<JSONObject>();
//		String codeKey = phoneCodeVO.getCodeKey();
//		String username = phoneCodeVO.getUsername();
		SysUser userByName = sysUserService.getUserByName(username);
		if(userByName==null){
			result.error500("用户名错误");
			return result;
		}
		String phoneNum = userByName.getPhone();

		Map<String,String> map = new HashMap<String,String>();
		JSONObject obj = new JSONObject();
		//判断是否已发送短信
		Object checkCode = redisUtil.get(phoneNum);
		System.out.println("验证码"+checkCode+"  手机号："+phoneNum);
		if(checkCode != null){
			obj.put("shortMessage","10分钟内的短信有效");
//			obj.put("key", phoneNum);
			result.setResult(obj);
			return result;
		}

		try {
			String code = RandomUtil.randomString("0123456789",6);
			String key = phoneNum;
			redisUtil.set(key, code, 600);
//			obj.put("key", key);
			//-------发送短信---------
			// 短信应用SDK AppID
			int appid = 1400304421; // 1400开头
			// 短信应用SDK AppKey
			String appkey = "b6e21d649eb57211b9b2401439a214a3";
			// 需要发送短信的手机号码
			String[] phoneNumbers = {phoneNum.toString()};
			// 短信模板ID，需要在短信应用中申请
			int templateId = 517361; // NOTE: 真实的模板ID需要在短信控制台中申请
			//templateId7839对应的内容是"您的验证码是: {1}"
			try {
				String[] params = {code.toString()};//数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
				SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
				SmsSingleSenderResult result1 = ssender.sendWithParam("86", phoneNumbers[0],
						templateId, params, "是杰安全云", "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
				obj.put("shortMessage","短信发送成功");
				result.setSuccess(true);
			} catch (HTTPException e) {
				// HTTP响应码错误
				e.printStackTrace();
				obj.put("shortMessage", "HTTP响应码错误");
				result.setSuccess(false);
			} catch (JSONException e) {
				// json解析错误
				e.printStackTrace();
				obj.put("shortMessage", "json解析错误");
				result.setSuccess(false);
			} catch (IOException e) {
				// 网络IO错误
				e.printStackTrace();
				obj.put("shortMessage", "网络IO错误");
				result.setSuccess(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
		}

		result.setResult(obj);
		return result;
	}

	@ApiOperation("客户短信验证码接口")
	@RequestMapping(value = "/shortMessage1", method = RequestMethod.POST)
	public Result<JSONObject> shortMessage1(@RequestParam("username") String username){
		Result<JSONObject> result = new Result<JSONObject>();
//		String codeKey = phoneCodeVO.getCodeKey();
//		String username = phoneCodeVO.getUsername();
		CustomerAccounts customerByName = customerAccountsService.getUserByName(username);
		if(customerByName==null){
			result.error500("用户名错误");
			return result;
		}
		String phoneNum = customerByName.getPhone();
		Object checkCode = redisUtil.get(phoneNum);
		Map<String,String> map = new HashMap<String,String>();
		JSONObject obj = new JSONObject();
		//判断是否已发送短信
		if(checkCode != null){
			obj.put("shortMessage","10分钟内的短信有效");
//			obj.put("key", codeKey);
			result.setResult(obj);
			return result;
		}

		try {
			String code = RandomUtil.randomString("0123456789",6);
			String key = phoneNum;
			redisUtil.set(key, code, 600);
//			map.put("key", key);
//			obj.put("key", key);
//			map.put("code",code);
			//-------发送短信---------
			// 短信应用SDK AppID
			int appid = 1400304421; // 1400开头
			// 短信应用SDK AppKey
			String appkey = "b6e21d649eb57211b9b2401439a214a3";
			// 需要发送短信的手机号码
//			System.out.println(phoneNum+"   "+code);
			String[] phoneNumbers = {phoneNum.toString()};
			// 短信模板ID，需要在短信应用中申请
			int templateId = 517361; // NOTE: 真实的模板ID需要在短信控制台中申请
			//templateId7839对应的内容是"您的验证码是: {1}"
			try {
				String[] params = {code.toString()};//数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
				SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
				SmsSingleSenderResult result1 = ssender.sendWithParam("86", phoneNumbers[0],
						templateId, params, "是杰安全云", "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
//				System.out.println(result1);
//				System.out.println("发送成功");
				result.setSuccess(true);
			} catch (HTTPException e) {
				// HTTP响应码错误
				e.printStackTrace();
				obj.put("shortMessage", "HTTP响应码错误");
				result.setSuccess(false);
			} catch (JSONException e) {
				// json解析错误
				e.printStackTrace();
				obj.put("shortMessage", "json解析错误");
				result.setSuccess(false);
			} catch (IOException e) {
				// 网络IO错误
				e.printStackTrace();
				obj.put("shortMessage", "网络IO错误");
				result.setSuccess(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
		}
		obj.put("shortMessage","短信发送成功");
		result.setResult(obj);
		return result;
	}
	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("用户退出登录接口")
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public Result<Object> logout(HttpServletRequest request,HttpServletResponse response) {
		//用户退出逻辑
		String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
		if(oConvertUtils.isEmpty(token)) {
			return Result.error("退出登录失败！");
		}
		String username = JwtUtil.getUsername(token);
		LoginUser sysUser = sysBaseAPI.getUserByName(username);
		if(sysUser!=null) {
			sysBaseAPI.addLog("用户名: "+sysUser.getRealname()+",退出成功！", CommonConstant.LOG_TYPE_1, null);
			log.info(" 用户名:  "+sysUser.getRealname()+",退出成功！ ");
			//清空用户登录Token缓存
			redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
			//清空用户登录Shiro权限缓存
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			return Result.ok("退出登录成功！");
		}else {
			return Result.error("Token无效!");
		}
	}

	@ApiOperation("客户退出登录接口")
	@RequestMapping(value = "/logout1", method = RequestMethod.GET)
	public Result<Object> logout1(HttpServletRequest request,HttpServletResponse response) {
		//用户退出逻辑
		String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
		if(oConvertUtils.isEmpty(token)) {
			return Result.error("退出登录失败！");
		}
		String username = JwtUtil.getUsername(token);
		CustomerAccounts userByName = customerAccountsService.getUserByName(username);
//		LoginUser sysUser = sysBaseAPI.getUserByName(username);
		if(userByName!=null) {
			sysBaseAPI.addLog("客户名: "+userByName.getAccount()+",退出成功！", CommonConstant.LOG_TYPE_1, null);
			log.info(" 用户名:  "+userByName.getAccount()+",退出成功！ ");
			//清空用户登录Token缓存
			redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
//			//清空用户登录Shiro权限缓存
//			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			return Result.ok("退出登录成功！");
		}else {
			return Result.error("Token无效!");
		}
	}

	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("loginfo")
	public Result<JSONObject> loginfo() {
		Result<JSONObject> result = new Result<JSONObject>();
		JSONObject obj = new JSONObject();
		//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		// 获取一天的开始和结束时间
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date dayStart = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date dayEnd = calendar.getTime();
		// 获取系统访问记录
		Long totalVisitCount = logService.findTotalVisitCount();
		obj.put("totalVisitCount", totalVisitCount);
		Long todayVisitCount = logService.findTodayVisitCount(dayStart,dayEnd);
		obj.put("todayVisitCount", todayVisitCount);
		Long todayIp = logService.findTodayIp(dayStart,dayEnd);
		//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		obj.put("todayIp", todayIp);
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}

	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("visitInfo")
	public Result<List<Map<String,Object>>> visitInfo() {
		Result<List<Map<String,Object>>> result = new Result<List<Map<String,Object>>>();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date dayEnd = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, -7);
		Date dayStart = calendar.getTime();
		List<Map<String,Object>> list = logService.findVisitCount(dayStart, dayEnd);
		result.setResult(oConvertUtils.toLowerCasePageList(list));
		return result;
	}


	/**
	 * 登陆成功选择用户当前部门
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/selectDepart", method = RequestMethod.PUT)
	public Result<JSONObject> selectDepart(@RequestBody SysUser user) {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = user.getUsername();
		if(oConvertUtils.isEmpty(username)) {
			LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
			username = sysUser.getUsername();
		}
		String orgCode= user.getOrgCode();
		this.sysUserService.updateUserDepart(username, orgCode);
		SysUser sysUser = sysUserService.getUserByName(username);
		JSONObject obj = new JSONObject();
		obj.put("userInfo", sysUser);
		result.setResult(obj);
		return result;
	}

	/**
	 * 短信登录接口
	 *
	 * @param jsonObject
	 * @return
	 */
	@PostMapping(value = "/sms")
	public Result<String> sms(@RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<String>();
		String mobile = jsonObject.get("mobile").toString();
		String smsmode = jsonObject.get("smsmode").toString();
		String[] phoneNums = {mobile};//手机号
		log.info(mobile);

		//随机数
		String captcha = RandomUtil.randomNumbers(6);
		String[] params = {captcha};//yanzhengma
		JSONObject obj = new JSONObject();

		Object object = redisUtil.get(mobile);
		if (object != null) {
			result.setMessage("验证码10分钟内，仍然有效！");
			result.setSuccess(false);
			return result;
		}
		obj.put("code", captcha);
		try {
			boolean b = false;
			//注册模板
			if (CommonConstant.SMS_TPL_TYPE_1.equals(smsmode)) {
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				if(sysUser!=null) {
					result.error500(" 手机号已经注册，请直接登录！");
					sysBaseAPI.addLog("手机号已经注册，请直接登录！", CommonConstant.LOG_TYPE_1, null);
					return result;
				}
//				b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
			}else {
				//登录模式，校验用户有效性
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				result = sysUserService.checkUserIsEffective(sysUser);
				if(!result.isSuccess()) {
					return result;
				}
				/**
				 * smsmode 短信模板方式  0 .登录模板、1.注册模板、2.忘记密码模板
				 */
				if (CommonConstant.SMS_TPL_TYPE_0.equals(smsmode)) {
					//登录模板
					String s = SendShortMessageUtil.sendShortMessage(517361, phoneNums, params);
					if("短信发送成功".equals(s)){
						b=true;
					}
//					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.LOGIN_TEMPLATE_CODE);
				} else if(CommonConstant.SMS_TPL_TYPE_2.equals(smsmode)) {
					//忘记密码模板
//					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
					String s = SendShortMessageUtil.sendShortMessage(517361, phoneNums, params);
					if("短信发送成功".equals(s)){
						b=true;
					}
				}
			}

			if (b == false) {
				result.setMessage("短信验证码发送失败,请稍后重试");
				result.setSuccess(false);
				return result;
			}else{
				result.setMessage("短信验证码发送成功,请及时输入");
			}
			//验证码10分钟内有效
			redisUtil.set(mobile, captcha, 600);
			//update-begin--Author:scott  Date:20190812 for：issues#391
			//result.setResult(captcha);
			//update-end--Author:scott  Date:20190812 for：issues#391
			result.setSuccess(true);

		} finally{}
//		catch (ClientException e) {
//			e.printStackTrace();
//			result.error500(" 短信接口未配置，请联系管理员！");
//			return result;
//		}
		return result;
	}
	/**
	 * 手机号登录接口
	 *
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("手机号登录接口")
	@PostMapping("/phoneLogin")
	public Result<JSONObject> phoneLogin(@RequestBody JSONObject jsonObject) {
		Result<JSONObject> result = new Result<JSONObject>();
		String phone = jsonObject.getString("mobile");
		//校验用户有效性
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		String smscode = jsonObject.getString("captcha");
		Object code = redisUtil.get(phone);
		if (!smscode.equals(code)) {
			result.setMessage("手机验证码错误");
			return result;
		}
		//用户信息
		userInfo(sysUser, result);
		//添加日志
		sysBaseAPI.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}


	/**
	 * 用户信息
	 *
	 * @param sysUser
	 * @param result
	 * @return
	 */
	private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
		String syspassword = sysUser.getPassword();
		String username = sysUser.getUsername();
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置token缓存有效时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
//		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);
		// 获取用户部门信息
		JSONObject obj = new JSONObject();
//		List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
//		obj.put("departs", departs);
//		if (departs == null || departs.size() == 0) {
//			obj.put("multi_depart", 0);
//		} else if (departs.size() == 1) {
//			sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
//			obj.put("multi_depart", 1);
//		} else {
//			obj.put("multi_depart", 2);
//		}
		Set<String> userRolesSet = sysUserService.getUserRolesSet(sysUser.getUsername());
		Set<String> userRoleNameSet = sysUserService.getRoleNameByUserName(sysUser.getUsername());
		boolean isPlatformRole = false;
		if(userRoleNameSet != null) {
			for(String ur :userRoleNameSet) {
				if(ur != null && ur.startsWith("是杰")) {
					isPlatformRole = true;
					break;
				}
			}
		}
		obj.put("roles",userRolesSet);
		if(isPlatformRole) {
			obj.put("logo", "files/20200328/未标题-1_1585384909082.png");
			obj.put("webName", "是杰安全云");
		} else {
			Customers customers = customersService.getLogo(sysUser.getId());
			if(customers != null) {
				obj.put("logo", customers.getCustLogo());
				obj.put("webName", customers.getWebName());
			}
		}
		obj.put("customerName", sysUser.getRealname());
		obj.put("token", token);
		obj.put("userId", sysUser.getId());
		obj.put("username",sysUser.getUsername());
		SysUser sysUser1 = new SysUser();
		sysUser1.setId(sysUser.getId());
		sysUser1.setUsername((sysUser.getUsername()));
		sysUser1.setRealname(sysUser.getRealname());
		obj.put("userInfo", sysUser1);
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}


	/**
	 * 客户信息
	 *
	 * @param customerAccount
	 * @param result
	 * @return
	 */
	private Result<JSONObject> costomerInfo(CustomerAccounts customerAccount, Result<JSONObject> result) {
		String syspassword = customerAccount.getPassword();
		String username = customerAccount.getAccount();
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置token缓存有效时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
//		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);
		JSONObject obj = new JSONObject();
		Customers customers = customersService.getById(customerAccount.getCustomerId());
		Integer oneAreaId = customers.getOneAreaId();//一级区域id
		String oneAreaName = areaService.getById(oneAreaId).getAreaName();//一级区域名
		Integer twoAreaId = customers.getTwoAreaId();//二级区域id
		String twoAreaName = areaService.getById(twoAreaId).getAreaName();//二级区域名
		String custLogo = customers.getCustLogo();//得到客户logo
		String biaoZhuLogo = customers.getBiaoZhuLogo();
		String webName = customers.getWebName();//web端名
		String customerName = customers.getName();//得到客户名
		String projectName = customers.getProjectName();//项目名
		BigDecimal latitude = customers.getLatitude();//得到纬度
		BigDecimal longitude = customers.getLongitude();//得到经度
		obj.put("token", token);
		obj.put("customerLogo",custLogo);
		obj.put("webName",webName);
		obj.put("customerId", customerAccount.getCustomerId());
		obj.put("biaoZhuLogo",biaoZhuLogo);
		obj.put("customerName",customerName);
		obj.put("oneAreaName",oneAreaName);
		obj.put("twoAreaName",twoAreaName);
		obj.put("longitude",longitude.toString());
		obj.put("latitude",latitude.toString());
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}
	/**
	 * 获取加密字符串
	 * @return
	 */
	@GetMapping(value = "/getEncryptedString")
	public Result<Map<String,String>> getEncryptedString(){
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		Map<String,String> map = new HashMap<String,String>();
		map.put("key", EncryptedString.key);
		map.put("iv",EncryptedString.iv);
		result.setResult(map);
		return result;
	}

	/**
	 * 获取校验码
	 */
	@ApiOperation("获取验证码")
	@GetMapping(value = "/getCheckCode")
	public Result<Map<String,String>> getCheckCode(){
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		Map<String,String> map = new HashMap<String,String>();
		try {
			String code = RandomUtil.randomString(BASE_CHECK_CODES,4);
			String key = MD5Util.MD5Encode(code+System.currentTimeMillis(), "utf-8");
			redisUtil.set(key, code, 60);
			map.put("key", key);
			map.put("code",code);
			result.setResult(map);
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * app登录
	 * @param sysLoginModel
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mLogin", method = RequestMethod.POST)
	public Result<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) throws Exception {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		//1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}

		String orgCode = sysUser.getOrgCode();
		if(oConvertUtils.isEmpty(orgCode)) {
			//如果当前用户无选择部门 查看部门关联信息
			List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
			if (departs == null || departs.size() == 0) {

				return result;
			}
			orgCode = departs.get(0).getOrgCode();
			sysUser.setOrgCode(orgCode);
			this.sysUserService.updateUserDepart(username, orgCode);
		}
		JSONObject obj = new JSONObject();
		//用户登录信息
		obj.put("userInfo", sysUser);

		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置超时时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
//		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);
		//token 信息
		obj.put("token", token);
		result.setResult(obj);
		result.setSuccess(true);
		result.setCode(200);
		sysBaseAPI.addLog("用户名: " + username + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

}