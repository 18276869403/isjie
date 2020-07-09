package org.jeecg.modules.demo.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.Area;
import org.jeecg.modules.demo.monitor.vo.AreaDTO;
import org.jeecg.modules.demo.monitor.vo.AreaXY;
import org.jeecg.modules.demo.monitor.vo.DeviceAreaVO;

import java.util.List;

/**
 * @author 鞠 玮
 * @date 2020/7/9 11:08
 */
public interface AreaFloorMapper extends BaseMapper<DeviceAreaVO> {

    /**
     * 根据客户id查询区域
     * @param customerId 客户id
     * @return 区域集合
     */
    List<DeviceAreaVO> findAllArea(@Param("customerId") Integer customerId);

    /**
     * 根据客户id查询区域点
     * @param customerId 客户id
     * @return  区域点列表
     */
    List<DeviceAreaVO> findAreaPoint(@Param("customerId") Integer customerId);

    /**
     * 获取课户的鸟瞰图
     * @param customerId 客户id
     * @return  鸟瞰图url
     */
    String getAerialView(@Param("customerId") Integer customerId);

    /**
     * 修改区域点坐标
     * @param areaXY 坐标
     * @return 条数
     */
    int updateAreaFloorPlan(AreaXY areaXY);

    /**
     * 增加区域点
     * @param areaXY 坐标
     * @return 条数
     */
    int saveAreaFloorPlan(AreaXY areaXY);

    /**
     * 删除区域点
     * @param id 区域点id
     */
    int deletePoint(Integer id);
}
