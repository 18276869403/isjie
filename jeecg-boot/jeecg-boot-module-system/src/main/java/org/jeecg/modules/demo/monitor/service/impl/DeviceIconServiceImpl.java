package org.jeecg.modules.demo.monitor.service.impl;

import org.jeecg.modules.demo.monitor.entity.DeviceIcon;
import org.jeecg.modules.demo.monitor.mapper.DeviceIconMapper;
import org.jeecg.modules.demo.monitor.service.IDeviceIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 设备图标表
 * @Author: jeecg-boot
 * @Date:   2019-12-23
 * @Version: V1.0
 */
@Service
@Transactional
public class DeviceIconServiceImpl extends ServiceImpl<DeviceIconMapper, DeviceIcon> implements IDeviceIconService {
    @Autowired
    private DeviceIconMapper deviceIconMapper;

    @Override
    public List<DeviceIcon> getPicByDeviceType(String type) {

        List<DeviceIcon> deviceIcons=deviceIconMapper.getPicByDeviceType(type);

        return deviceIcons;
    }

}
