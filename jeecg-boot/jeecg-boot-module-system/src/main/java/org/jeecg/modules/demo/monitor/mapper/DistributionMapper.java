package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.Distribution;
import org.jeecg.modules.demo.monitor.vo.DeviceVo;
import org.jeecg.modules.demo.monitor.vo.DistributionVo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.demo.monitor.vo.LoadCustomersVo;
import org.jeecg.modules.system.entity.SysUser;


/**
 * @Description: 分配客户表
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
public interface DistributionMapper extends BaseMapper<Distribution> {
	IPage<DistributionVo> custoDistribution(Page<DistributionVo> page, @Param(Constants.WRAPPER) QueryWrapper<DistributionVo> queryWrapper); 
	//根据用户分配客户
	public List<Distribution> queryAll(@Param("userId") String userId);

	List<LoadCustomersVo> findAll(@Param("userId") String id);

	List<SysUser> findUsernameByCustomerId(@Param("customerId") Integer customerId);

    String queryBycid(@Param("customerId")Integer customerId);
}
