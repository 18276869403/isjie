package org.jeecg.modules.demo.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.demo.monitor.mapper.AreaFloorMapper;
import org.jeecg.modules.demo.monitor.service.IAreaFloorService;
import org.jeecg.modules.demo.monitor.service.IAreaService;
import org.jeecg.modules.demo.monitor.vo.AreaXY;
import org.jeecg.modules.demo.monitor.vo.DeviceAreaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 鞠 玮
 * @date 2020/7/9 11:34
 */
@Service
public class AreaFloorServiceImpl extends ServiceImpl<AreaFloorMapper,DeviceAreaVO> implements IAreaFloorService {
    @Autowired
    AreaFloorMapper areaFloorMapper;
    /**
     * 根据客户id查询区域
     *
     * @return
     */
    @Override
    public List<DeviceAreaVO> findAllArea(Integer customerId) {
        return areaFloorMapper.findAllArea(customerId);
    }

    /**
     * 根据客户id查询区域点
     *
     * @param customerId 客户id
     * @return 区域点列表
     */
    @Override
    public List<DeviceAreaVO> findAreaPoint(Integer customerId) {
        return areaFloorMapper.findAreaPoint(customerId);
    }

    /**
     * 获取课户的鸟瞰图
     *
     * @param customerId 客户id
     * @return 鸟瞰图url
     */
    @Override
    public String getAerialView(Integer customerId) {
        return areaFloorMapper.getAerialView(customerId);
    }

    /**
     * 增加区域点
     *
     * @param areaXY 坐标
     * @return 条数
     */
    @Override
    public boolean saveAreaFloorPlan(AreaXY areaXY) {
        return areaFloorMapper.saveAreaFloorPlan(areaXY) == 1;
    }

    /**
     * 修改区域点坐标
     *
     * @param areaXY 坐标
     * @return 条数
     */
    @Override
    public int updateAreaFloorPlan(AreaXY areaXY) {
        return areaFloorMapper.updateAreaFloorPlan(areaXY);
    }

    /**
     * 删除区域点
     *
     * @param id 区域点id
     */
    @Override
    public int deletePoint(Integer id) {
        return areaFloorMapper.deletePoint(id);
    }
}
