package org.jeecg.modules.demo.monitor.service.impl;

import org.jeecg.modules.demo.monitor.entity.OneArea;
import org.jeecg.modules.demo.monitor.entity.TwoArea;
import org.jeecg.modules.demo.monitor.mapper.OneAreaMapper;
import org.jeecg.modules.demo.monitor.mapper.TwoAreaMapper;
import org.jeecg.modules.demo.monitor.service.IOneAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 一级区域表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Service
@Transactional
public class OneAreaServiceImpl extends ServiceImpl<OneAreaMapper, OneArea> implements IOneAreaService {
	@Autowired
    private TwoAreaMapper twoAreaMapper;

    @Override
    public boolean deleteOne(String oneId) {
        //1.删除一级区域
        this.removeById(oneId);
        //2.批量删除对应二级区域
        LambdaQueryWrapper<TwoArea> query = new LambdaQueryWrapper<TwoArea>();
        query.eq(TwoArea::getOneAreaId, oneId);
        twoAreaMapper.delete(query);
        return false;
    }
}
