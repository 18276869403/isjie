package org.jeecg.modules.demo.monitor.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.DeviceArea;
import org.jeecg.modules.demo.monitor.vo.DeviceAreaVO;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface IDeviceAreaService extends IService<DeviceArea> {
	IPage<DeviceAreaVO> custoMpage(Page<DeviceAreaVO> page, @Param(Constants.WRAPPER) QueryWrapper<DeviceAreaVO> queryWrapper);
}
