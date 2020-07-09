package org.jeecg.modules.demo.monitor.service.impl;

import org.jeecg.modules.demo.monitor.entity.CustomerAccounts;
import org.jeecg.modules.demo.monitor.entity.Customers;
import org.jeecg.modules.demo.monitor.mapper.CustomersMapper;
import org.jeecg.modules.demo.monitor.service.ICustomersService;
import org.jeecg.modules.demo.monitor.vo.CustomersVo;
import org.jeecg.modules.demo.monitor.vo.FloorPlanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 客户表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Service
public class CustomersServiceImpl extends ServiceImpl<CustomersMapper, Customers> implements ICustomersService {

	@Autowired
	private CustomersMapper customersMapper;
	
	@Override
	public IPage<CustomersVo> queryCustomersVoAll(Page<CustomersVo> page, Wrapper<CustomersVo> wrapper) {
		return customersMapper.queryCustomersVoAll(page, wrapper);
	}

	@Override
	public List<CustomerAccounts> findCustomerAccounts(Integer customerId) {
		return customersMapper.findCustomerAccounts(customerId);
	}

    @Override
    public Boolean customerIfFloorMapByCid(Integer cid) {
		List<FloorPlanDTO> list=customersMapper.customerIfFloorMapByCid(cid);

		int count = 0;

		for (FloorPlanDTO floorPlanDTO : list) {
			if (floorPlanDTO != null) {
				if (floorPlanDTO.getPlanPic() != null) {
					count++;
				}
			}

		}



		return count > 0;
    }

	@Override
	public Customers findCustomerByName(String name) {
		return customersMapper.findCustomerByName(name);
	}

	@Override
	public Customers getLogo(String id) {
		return customersMapper.getLogo(id);
	}


}
