package org.jeecg.modules.demo.monitor.service;

import org.jeecg.modules.demo.monitor.entity.DeviceIcon;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 设备图标表
 * @Author: jeecg-boot
 * @Date:   2019-12-23
 * @Version: V1.0
 */
public interface IDeviceIconService extends IService<DeviceIcon> {
    List<DeviceIcon> getPicByDeviceType(String type);

}
