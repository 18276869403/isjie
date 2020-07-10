package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.DeviceArea;
import org.jeecg.modules.demo.monitor.vo.DeviceAreaVO;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 设备区域
 * @Author: jeecg-boot
 * @Date: 2019-12-20
 * @Version: V1.0
 */
public interface DeviceAreaMapper extends BaseMapper<DeviceArea> {
	IPage<DeviceAreaVO> custoMpage(Page<DeviceAreaVO> page, @Param(Constants.WRAPPER) QueryWrapper<DeviceAreaVO> queryWrapper);

	List<DeviceAreaVO> exportXls(@Param(Constants.WRAPPER) QueryWrapper<DeviceArea> queryWrapper);
}
