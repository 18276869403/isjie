package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.DeviceIcon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 设备图标表
 * @Author: jeecg-boot
 * @Date:   2019-12-23
 * @Version: V1.0
 */
public interface DeviceIconMapper extends BaseMapper<DeviceIcon> {
    List<DeviceIcon> getPicByDeviceType(@Param("type") String type);

    DeviceIcon getPicByDeviceTypeAndStatus(@Param("type")String deviceType,@Param("status") String statusType);
}
