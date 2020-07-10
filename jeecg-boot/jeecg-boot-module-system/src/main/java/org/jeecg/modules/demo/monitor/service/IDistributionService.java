package org.jeecg.modules.demo.monitor.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.Distribution;
import org.jeecg.modules.demo.monitor.vo.AreaDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceVo;
import org.jeecg.modules.demo.monitor.vo.DistributionVo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.monitor.vo.LoadCustomersVo;
import org.jeecg.modules.system.entity.SysUser;

import java.util.List;

/**
 * @Description: 分配客户表
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
public interface IDistributionService extends IService<Distribution> {
	IPage<DistributionVo> custoDistribution(Page<DistributionVo> page, @Param(Constants.WRAPPER) QueryWrapper<DistributionVo> queryWrapper);


	List<LoadCustomersVo> findAllByUserId(String uid);

	List<SysUser> findUsernameByCustomerId(Integer customerId);
}
