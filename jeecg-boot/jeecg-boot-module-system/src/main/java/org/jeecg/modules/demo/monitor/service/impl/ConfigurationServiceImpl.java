package org.jeecg.modules.demo.monitor.service.impl;

import org.jeecg.modules.demo.monitor.entity.Configuration;
import org.jeecg.modules.demo.monitor.mapper.ConfigurationMapper;
import org.jeecg.modules.demo.monitor.service.IConfigurationService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Service
public class ConfigurationServiceImpl extends ServiceImpl<ConfigurationMapper,Configuration > implements IConfigurationService {

}
