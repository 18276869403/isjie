package org.jeecg.modules.demo.monitor.service.impl;

import org.jeecg.modules.demo.monitor.entity.CustomerAccounts;
import org.jeecg.modules.demo.monitor.mapper.CustomerAccountsMapper;
import org.jeecg.modules.demo.monitor.service.ICustomerAccountsService;
import org.jeecg.modules.demo.monitor.vo.CustomersAccountsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 客户账号表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Service
public class CustomerAccountsServiceImpl extends ServiceImpl<CustomerAccountsMapper, CustomerAccounts> implements ICustomerAccountsService {

	@Autowired
	private CustomerAccountsMapper customerAccountsMapper;
	@Autowired
	private ISysBaseAPI sysBaseAPI;

	@Override
	public IPage<CustomersAccountsVo> queryCustAccountsVoAll(Page<CustomersAccountsVo> page,
			Wrapper<CustomersAccountsVo> wrapper) {
		// TODO Auto-generated method stub
		return customerAccountsMapper.queryCustAccountsVoAll(page, wrapper);
	}
	
	@Override
	public CustomerAccounts getUserByName(String username) {
		return customerAccountsMapper.getUserByName(username);
	}

	@Override
	public Result checkUserIsEffective(CustomerAccounts customerAccounts) {
		Result<?> result = new Result<Object>();
		//情况1：根据用户信息查询，该用户不存在
		if (customerAccounts == null) {
			result.error500("该用户不存在，请注册");
			sysBaseAPI.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
			return result;
		}
		//情况2：根据用户信息查询，该用户已注销
		if ("2".equals(customerAccounts.getCustState().toString())) {
			sysBaseAPI.addLog("用户登录失败，用户名:" + customerAccounts.getAccount() + "已注销！", CommonConstant.LOG_TYPE_1, null);
			result.error500("该用户已冻结");
			return result;
		}
//		//情况3：根据用户信息查询，该用户已冻结
//		if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
//			sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
//			result.error500("该用户已冻结");
//			return result;
//		}
		return result;
	}

	@Override
	public CustomerAccounts getCustomerId(String id) {
		return customerAccountsMapper.getCustomerId(id);
	}


}
