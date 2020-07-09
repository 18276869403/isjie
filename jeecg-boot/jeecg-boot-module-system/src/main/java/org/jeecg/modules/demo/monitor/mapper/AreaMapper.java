package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.Area;
import org.jeecg.modules.demo.monitor.vo.AreaVo;
import org.jeecg.modules.demo.monitor.vo.AreaVo2;
import org.jeecg.modules.demo.monitor.vo.CustomersVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.demo.monitor.vo.DropdownVo;

/**
 * @Description: 省市表
 * @Author: jeecg-boot
 * @Date:   2020-01-03
 * @Version: V1.0
 */
public interface AreaMapper extends BaseMapper<Area> {

	//城市列表信息
	public IPage<AreaVo> queryAreaVoAll(Page<AreaVo> page, @Param(Constants.WRAPPER) Wrapper<AreaVo> wrapper);
	
	//根据省份查询城市
	List<Map<String, Object>> findListByNum(@Param("num") Integer num);

	/**
	 * 根据用id删除所有客户
	 * @param useId
	 * @return
	 */
    int deleteAreaAll(@Param("useId") String useId);

	/**
	 * 根据用户id，客户id添加到关联表
	 * @param useId
	 * @param id
	 */
	void addAll(@Param("useId") String useId, @Param("id") Long id);

	/**
	 * 根据用户id查询所有的客户id集合
	 * @param uid
	 * @return
	 */
	List<Long> findCustomerByUid(@Param("uid") String uid);

	/**
	 * 查询所有的区域id
	 * @return
	 */
	List<Integer> findAllAid();
	//根据省份查询省份信息
		public Area querAreaByNum(@Param("num") Integer num);


	//根据用户id省份信息
	public List<DropdownVo> querOneAreaByUid(@Param("uid") String uid);
	//根据用户id城市信息
	public List<DropdownVo> querTowAreaByUid(@Param("provinceid") Integer provinceid, @Param("uid") String uid);
	//根据用户id客户信息
	public List<DropdownVo> querCustomerByUid(@Param("provinceid") Integer provinceid, @Param("cityid") Integer cityid, @Param("uid") String uid);


	/**
	 * 查询所有的cid
	 * @return
	 */
	List<Integer> findAllCid();
}
