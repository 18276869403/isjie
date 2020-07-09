package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.CustomerAccounts;
import org.jeecg.modules.demo.monitor.entity.Customers;
import org.jeecg.modules.demo.monitor.vo.CustomersVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.demo.monitor.vo.FloorPlanDTO;
import org.jeecg.modules.demo.monitor.vo.LoadCustomersVo;

/**
 * @Description: 客户表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface CustomersMapper extends BaseMapper<Customers> {

	//客户列表信息
	public IPage<CustomersVo> queryCustomersVoAll(Page<CustomersVo> page, @Param(Constants.WRAPPER) Wrapper<CustomersVo> wrapper);
	//根据省市查询用户
	List<Map<String, Object>> findCutByAreaId(@Param("areaId") int areaId);
	Customers findCustomerNameById(@Param("customerId") Integer customerId);
	
	//查询客户账号
	public CustomersVo queryAllByCustId(@Param("customerId") int customerId);
	//根据id查询
	  CustomersVo queryCustomersVoAllById(@Param("id") Integer id);
	  
	// 根据id查客户设备报警总数量
			int queryDeviceAlarmById(@Param("id") Integer id);

	/**
	 * 根据用户id查询所有的客户id集合
	 * @param uid
	 * @return
	 */
    List<Integer> queryCustomerIdByUid(@Param("uid") String uid);


	//图标id查询省份客户列
	List<LoadCustomersVo> queryOneVoAllById(@Param("pid") Integer pid, @Param("uid") String uid);

	//图标id查询城市客户列
	List<LoadCustomersVo> queryTowVoAllById(@Param("pid") Integer pid, @Param("cid") Integer cid, @Param("uid") String uid);
	//图标id查询客户
	List<LoadCustomersVo> queryCustomersVoAllBycustomerId(@Param("customerId") Integer customerId);

	//根据客户id查询客户账号
	List<CustomerAccounts> findCustomerAccounts(@Param("customerId") Integer customerId);

	List<FloorPlanDTO> customerIfFloorMapByCid(@Param("cid") Integer cid);

    Customers findCustomerByName(String name);

    Customers getLogo(@Param("cid")String id);
}
