package org.jeecg.modules.demo.monitor.service.impl;

import org.jeecg.modules.demo.monitor.entity.Distribution;
import org.jeecg.modules.demo.monitor.mapper.DistributionMapper;
import org.jeecg.modules.demo.monitor.service.IDistributionService;
import org.jeecg.modules.demo.monitor.vo.AreaDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceVo;
import org.jeecg.modules.demo.monitor.vo.DistributionVo;
import org.jeecg.modules.demo.monitor.vo.LoadCustomersVo;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 分配客户表
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
@Service
public class DistributionServiceImpl extends ServiceImpl<DistributionMapper, Distribution> implements IDistributionService {
@Autowired DistributionMapper distributionMapper;

	@Override
	public IPage<DistributionVo> custoDistribution(Page<DistributionVo> page, QueryWrapper<DistributionVo> queryWrapper) {
		/* return distributionMapper.custoDistribution(page, queryWrapper); */
		return distributionMapper.custoDistribution(page, queryWrapper);
	}

	@Override
	public List<LoadCustomersVo> findAllByUserId(String uid) {
		List<LoadCustomersVo> distributions = distributionMapper.findAll(uid);
		return distributions;
	}


	@Override
	public List<SysUser> findUsernameByCustomerId(Integer customerId) {
		List<SysUser> sysUser = distributionMapper.findUsernameByCustomerId(customerId);
		return sysUser;
	}


}
