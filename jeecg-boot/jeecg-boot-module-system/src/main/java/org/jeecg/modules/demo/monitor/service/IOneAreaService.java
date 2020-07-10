package org.jeecg.modules.demo.monitor.service;

import org.jeecg.modules.demo.monitor.entity.OneArea;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 一级区域表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface IOneAreaService extends IService<OneArea> {
	boolean deleteOne(String id);
}
