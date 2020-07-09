package org.jeecg.modules.demo.monitor.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.Area;
import org.jeecg.modules.demo.monitor.vo.AreaDTO;
import org.jeecg.modules.demo.monitor.vo.AreaVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 省市表
 * @Author: jeecg-boot
 * @Date:   2020-01-03
 * @Version: V1.0
 */
public interface IAreaService extends IService<Area> {

	List<Map<String, Object>> findOrgUserTree(int num);
	
	//城市列表信息
	public IPage<AreaVo> queryAreaVoAll(Page<AreaVo> page, @Param(Constants.WRAPPER) Wrapper<AreaVo> wrapper);
	
	boolean deleteOne(String id);

    Boolean updateCustomersArea(AreaDTO areaDTO);

	List<Long> findCustomerByUid(String uid);

	List<Integer> findAllAid();
}
