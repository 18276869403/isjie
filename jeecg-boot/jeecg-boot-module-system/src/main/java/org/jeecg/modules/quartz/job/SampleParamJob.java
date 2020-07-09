package org.jeecg.modules.quartz.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jeecg.modules.demo.monitor.entity.AlarmSms;
import org.jeecg.modules.demo.monitor.entity.CustomerAccounts;
import org.jeecg.modules.demo.monitor.entity.Customers;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.service.IAlarmSmsService;
import org.jeecg.modules.demo.monitor.service.ICustomersService;
import org.jeecg.modules.demo.monitor.service.IDeviceService;
import org.jeecg.modules.demo.monitor.service.IDistributionService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.util.SendShortMessageUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * 示例带参定时任务
 *
 * @Author Scott
 */
@Slf4j
public class SampleParamJob implements Job {

	/**
	 * 若参数变量名修改 QuartzJobController中也需对应修改
	 */
	private String parameter;

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	@Autowired
	private IAlarmSmsService alarmSmsService;
	@Autowired
	private ICustomersService customersService;
	@Autowired
	private IDistributionService distributionService;
	@Autowired
	private IDeviceService deviceService;

	private static Map<String,String> map;
	//是否开启短信功能
	private final static boolean isEnable = true;

	static{
		map = new HashMap<>();
		map.put("1","探测器");
		map.put("2","控制器");
		map.put("3","输出模块");
	}
	private static int TemplateId = 534150;
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		if(!isEnable) {
			log.warn("未开启发送短信" );
			return;
		}
		//		System.out.println("--------------------进入定时任务--------------------------");
		//查出所有未发送短信的报警信息
		List<AlarmSms> allAlarms = alarmSmsService.findAllAlarm();
		log.error("处理待发送短信：" + allAlarms.size());
		if(allAlarms.size()==0){
//			System.out.println("所有报警短信已发送完毕");
			return;
		}
		//报警短信发送
//		try {
		for (AlarmSms alarmSms :allAlarms) {
			//对可以拿到的进行赋值
			//报警时间
			String alarmdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(alarmSms.getAlarmDate());
			//发送时间
			Date date = new Date();
			//报警类型
			String alarmType = alarmSms.getAlarmType().replace("发生","");
			//设备类型
			String deviceType = map.get(alarmSms.getEquipType());
			List<String> customerPhones = new ArrayList<>();//客户手机号
			List<String> customerParam = new ArrayList<>();//客户参数
//			List<String> UserPhones = new ArrayList<>();//用户手机号
			List<String> userParam = new ArrayList<>();//用户参数
//			System.out.println("alarmSms"+alarmSms);
			//获得报警设备
			Device device = deviceService.getById(alarmSms.getDeviceId()); //;alarmSmsService.findDevice(alarmSms);
			if(device == null){
//				System.out.println("无法找到对应设备"+alarmSms);
				continue;
			}
			//获得设备位号
			String deviceNum = device.getDeviceNum();
//			System.out.println("设备位号：  "+device.getDeviceNum());
			//获得设备类型
//			System.out.println("设备已获得："+device);
			//获得对应客户
			Customers customers = customersService.getById(device.getCustomerId());
			if(customers == null || 
					// 被删除
					(customers.getDelFlag() != null && customers.getDelFlag().intValue() == 1)) {
				continue;
			}
//			System.out.println("客户已获得："+customers);
			//获得客户账号
			List<CustomerAccounts> customerAccountsList = customersService.findCustomerAccounts(customers.getId());
//			System.out.println("客户账号已获得："+customerAccountsList);
			//向客户发送短信
			//添加参数
			//添加客户名 1
			customerParam.add(customers.getName());
			//添加设备类型 2
			customerParam.add(deviceType);
			//添加设备位号 3
			customerParam.add(deviceNum);
			//添加报警时间 4
			customerParam.add(alarmdate);
			//添加报警类型 5
			customerParam.add(alarmType);
			//添加设备imei 6
			customerParam.add(alarmSms.getImei());
			//添加设备ICCID 7
			customerParam.add(alarmSms.getIccid());
			//添加设备地址add 8
			customerParam.add(alarmSms.getEquipAdd());
			//添加设备安装位置 9
			if(device.getInstallationPosition()==null){
				customerParam.add("-");
			}else{
				customerParam.add(device.getInstallationPosition());
			}
			//添加手机号
			for (CustomerAccounts customerAccount: customerAccountsList) {
				customerPhones.add(customerAccount.getPhone());
				AlarmSms alarmSms1 = new AlarmSms();
				alarmSms1.setAlarmDate(alarmSms.getAlarmDate());//报警时间
				alarmSms1.setAlarmType(alarmSms.getAlarmType());//报警类型
				alarmSms1.setEquipType(alarmSms.getEquipType());
				alarmSms1.setEquipAdd(alarmSms.getEquipAdd());
				alarmSms1.setImei(alarmSms.getImei());
				alarmSms1.setBackup1(customerAccount.getPhone());
				alarmSms1.setIccid(alarmSms.getIccid());
				alarmSms1.setSendDate(date);
				alarmSms1.setSmsType("1");
				//添加报警短信数据
				alarmSmsService.save(alarmSms1);
			}
			//发送短信
			SendShortMessageUtil.sendShortMessage(TemplateId,
					customerPhones.toArray(new String[customerPhones.size()]),
					customerParam.toArray(new String[9]));
//				System.out.println("客户param数量："+customerParam.size());
//				System.out.println("客户手机号数量："+customerPhones.size());
//				System.out.println("客户短信编号"+CustomertemplateId);
//				System.out.println("客户手机号"+customerPhones.toArray(new String[customerPhones.size()]).toString());
//				System.out.println("客户短信信息"+customerParam.toArray(new String[customerParam.size()]).toString());
			//获得对应用户(可能会有多个)
			List<SysUser> sysUsers = distributionService.findUsernameByCustomerId(customers.getId());

			//向用户发送短信
			for (SysUser sysUser:sysUsers) {
				//添加客户名1
				userParam.add(sysUser.getRealname());
				//添加设备类型2
				userParam.add(deviceType);
				//添加设备位号 3
				userParam.add(deviceNum);
				//添加报警时间 4
				userParam.add(alarmdate);
				//添加报警类型 5
				userParam.add(alarmType);
				//添加设备imei 6
				userParam.add(alarmSms.getImei());
				//添加设备ICCID 7
				userParam.add(alarmSms.getIccid());
				//添加设备地址add 8
				userParam.add(alarmSms.getEquipAdd());
				//添加设备安装位置 9
				if(device.getInstallationPosition()==null){
					userParam.add("-");
				}else{
					userParam.add(device.getInstallationPosition());
				}
				String[] ss = {sysUser.getPhone()};
//					System.out.println("用户手机号："+sysUser.getPhone());
				//发送短信
//				System.out.println("用户param数量："+userParam.size());
				SendShortMessageUtil.sendShortMessage(TemplateId,ss,userParam.toArray(new String[9]));
//					System.out.println("用户短信信息"+userParam.toArray(new String[userParam.size()]).toString());
//					System.out.println("用户手机号"+ss.toString());
//					System.out.println("用户短信编号"+UserTemplateId);
				userParam.clear();
			}
			//短信已发送，修改数据库信息

			alarmSmsService.updateStatus(alarmSms.getId(),date);

		}
//		}finally{
//			System.out.println("出现错误");
//		}
		log.error("待发送短信处理结算");
	}
}
