package org.jeecg.modules.demo.monitor.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.CustomerAccounts;
import org.jeecg.modules.demo.monitor.entity.Customers;
import org.jeecg.modules.demo.monitor.vo.CustomersVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 客户表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface ICustomersService extends IService<Customers> {

	//客户列表信息
	public IPage<CustomersVo> queryCustomersVoAll(Page<CustomersVo> page, @Param(Constants.WRAPPER) Wrapper<CustomersVo> wrapper);
	List<CustomerAccounts> findCustomerAccounts(Integer id);

	Boolean customerIfFloorMapByCid(Integer cid);

	Customers findCustomerByName(String name);

    Customers getLogo(String id);
}
