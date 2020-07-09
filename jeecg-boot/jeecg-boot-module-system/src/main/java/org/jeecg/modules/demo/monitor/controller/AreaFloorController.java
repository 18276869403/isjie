package org.jeecg.modules.demo.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.demo.monitor.entity.DeviceArea;
import org.jeecg.modules.demo.monitor.service.IAreaFloorService;
import org.jeecg.modules.demo.monitor.service.IAreaService;
import org.jeecg.modules.demo.monitor.service.IDeviceAreaService;
import org.jeecg.modules.demo.monitor.vo.AreaXY;
import org.jeecg.modules.demo.monitor.vo.DeviceAreaVO;
import org.jeecg.modules.demo.monitor.vo.DeviceXY;
import org.jeecg.modules.demo.monitor.vo.XyAxisDTO;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 鞠 玮
 * @date 2020/7/9 11:05
 */
@Slf4j
@Api(tags="区域布点")
@RestController
@RequestMapping("/monitor/areaFloor")
public class AreaFloorController extends JeecgController<DeviceAreaVO, IAreaFloorService> {
    @Autowired
    IAreaFloorService areaFloorService;

    @ApiOperation(value = "根据客户id获取区域列表和鸟瞰图", notes = "根据客户id获取区域列表和鸟瞰图")
    @GetMapping("/areas/{customerId}")
    public Result<?> getAreaByCustomerId(@PathVariable Integer customerId) {
        // 获取区域列表
        List<DeviceAreaVO> allArea = areaFloorService.findAllArea(customerId);
        // 获取鸟瞰图
        String aerialView = areaFloorService.getAerialView(customerId);
        Map<String, Object> map = new HashMap<>();
        map.put("areas", allArea);
        map.put("aerialView", aerialView);
        return Result.ok(map);
    }
    @ApiOperation(value = "根据客户id获取区域点列表和鸟瞰图", notes = "根据客户id获取区域点列表和鸟瞰图")
    @GetMapping("/areasPoint/{customerId}")
    public Result<?> getAreaPoint(@PathVariable Integer customerId) {
        // 获取区域列表
        return Result.ok(areaFloorService.findAreaPoint(customerId));
    }

    /**
     * 接收区域图标xy位置，保存起来
     * @param
     * @return
     */
    @PostMapping("savePoint")
    @ApiOperation(value = "接收区域图标xy位置，保存起来", notes = "接收区域图标xy位置，保存起来")
    public Result<?> saveDeviceXY(@RequestBody List<AreaXY> areaXYs) {
        System.out.println("区域标记：" + areaXYs);
        areaXYs.forEach(areaXY -> {
            if(areaXY.getId()!=null){
                if(areaXY.getXaxis()=="null"){
                    areaXY.setXaxis(null);
                    areaXY.setYaxis(null);
                }
                areaFloorService.updateAreaFloorPlan(areaXY);
            }
        });
        return Result.ok();
    }

    @PostMapping("/saveDeviceFloor")
    @ApiOperation(value = "保存区域", notes = "保存区域")
    public Result<?> saveDeviceFloor(@RequestBody AreaXY areaXY) {
        System.out.println("坐标:"+areaXY);
        if (areaXY != null) {
            Boolean b=  areaFloorService.saveAreaFloorPlan(areaXY);
            if (b) {
                return Result.ok();
            }
        }
        return Result.error("添加失败");

    }

    @DeleteMapping("/area/{id}")
    @ApiOperation(value = "删除区域标记", notes = "区域标记")
    public Result<?> deletePoint(@PathVariable Integer id) {
        return Result.ok(areaFloorService.deletePoint(id));
    }

}
