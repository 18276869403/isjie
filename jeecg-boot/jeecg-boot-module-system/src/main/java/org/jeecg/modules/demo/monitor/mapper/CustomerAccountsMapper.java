package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.CustomerAccounts;
import org.jeecg.modules.demo.monitor.vo.CustomersAccountsVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 客户账号表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface CustomerAccountsMapper extends BaseMapper<CustomerAccounts> {

	//查询客户账号列表信息
	public IPage<CustomersAccountsVo> queryCustAccountsVoAll(Page<CustomersAccountsVo> page, @Param(Constants.WRAPPER) Wrapper<CustomersAccountsVo> wrapper);

	//找到客户信息
    CustomerAccounts getUserByName(@Param("username") String username);


	CustomerAccounts getCustomerId(@Param("customerId")String id);
}
