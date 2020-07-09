package org.jeecg.modules.demo.monitor.service.impl;

import org.jeecg.modules.demo.monitor.entity.DeviceArea;
import org.jeecg.modules.demo.monitor.mapper.DeviceAreaMapper;
import org.jeecg.modules.demo.monitor.service.IDeviceAreaService;
import org.jeecg.modules.demo.monitor.vo.DeviceAreaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date: 2019-12-20
 * @Version: V1.0
 */
@Service
@Transactional
public class DeviceAreaServiceImpl extends ServiceImpl<DeviceAreaMapper, DeviceArea> implements IDeviceAreaService {
    @Autowired
    private DeviceAreaMapper deviceAreaMapper;

	@Override
	public IPage<DeviceAreaVO> custoMpage(Page<DeviceAreaVO> page, QueryWrapper<DeviceAreaVO> queryWrapper) {
		return deviceAreaMapper.custoMpage(page, queryWrapper);
	}

}
