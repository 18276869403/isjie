package org.jeecg.modules.demo.monitor.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.monitor.entity.CustomerAccounts;
import org.jeecg.modules.demo.monitor.vo.CustomersAccountsVo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 客户账号表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface ICustomerAccountsService extends IService<CustomerAccounts> {

	//查询客户账号列表信息
	public IPage<CustomersAccountsVo> queryCustAccountsVoAll(Page<CustomersAccountsVo> page, @Param(Constants.WRAPPER) Wrapper<CustomersAccountsVo> wrapper);

	CustomerAccounts getUserByName(String username);

	Result checkUserIsEffective(CustomerAccounts customerAccounts);

    CustomerAccounts getCustomerId(String id);
}
