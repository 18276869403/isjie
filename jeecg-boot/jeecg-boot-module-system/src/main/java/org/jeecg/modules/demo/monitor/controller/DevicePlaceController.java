package org.jeecg.modules.demo.monitor.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.demo.monitor.entity.DeviceIcon;
import org.jeecg.modules.demo.monitor.entity.FloorPlan;
import org.jeecg.modules.demo.monitor.service.IDeviceIconService;
import org.jeecg.modules.demo.monitor.service.IDevicePlaceService;
import org.jeecg.modules.demo.monitor.service.IDeviceService;
import org.jeecg.modules.demo.monitor.service.IFloorPlanService;
import org.jeecg.modules.demo.monitor.vo.DeviceDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceFloor;
import org.jeecg.modules.demo.monitor.vo.DevicePlace;
import org.jeecg.modules.demo.monitor.vo.DeviceXY;
import org.jeecg.modules.demo.monitor.vo.XyAxisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@Api(tags = "设备楼层表")
@RequestMapping("/devicePlace")
public class DevicePlaceController extends JeecgController<FloorPlan, IDevicePlaceService> {
    @Autowired
    private IDevicePlaceService devicePlaceService;
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IDeviceIconService deviceIconService;
    @Autowired
    private IFloorPlanService floorPlanService;


    /**
     * 根据客户id，楼层id查询楼层和设备信息
     * @param id
     * @param Fid
     * @return
     */
    @GetMapping("findDevicePlaceByFloorPlanId")
    @ApiOperation(value = "根据客户id，楼层id查询楼层和设备信息", notes = "根据客户id，楼层id查询楼层和设备信息")
    public Result<?> findDevicePlaceByFloorPlanId(
    		HttpServletRequest req,
    		@RequestParam(value = "id",required = false) Integer id,
    		@RequestParam("Fid")Integer Fid) {
        Result<DevicePlace> result = new Result<>();
        FloorPlan floorPlan = floorPlanService.getById(Fid);
        DevicePlace devicePlace = devicePlaceService.findDevicePlaceByFloorPlanId(floorPlan.getCustomerId(),
        		Fid,
        		super.getLoginUser(req).getId());
        //接收用户名，set进去
        result.setResult(devicePlace);
        return result;
    }

    /**
     * 根据设备id,查询当前设备是否布置
     *
     */
    @GetMapping("findFloorByDeviceId")
    @ApiOperation(value = "根据设备id,查询当前设备是否布置", notes = "根据设备id,查询当前设备是否布置")
    public Result<?> findFloorByDeviceId(@RequestParam(value = "did",required = false) Integer id) {
        Result<List<Integer>> result = new Result<>();
        List<Integer> fid = deviceService.findFloorByDeviceId(id);
        //接收用户名，set进去
        result.setResult(fid);
        return result;
    }

    /**
     * 根据楼层id动态查询楼层图和设备
     * @param Fid
     * @return
     */
    @GetMapping("findDeviceAndFloorPlanByFId")
    @ApiOperation(value = "根据楼层id动态查询楼层图和设备", notes = "根据楼层id动态查询楼层图和设备")
    public Result<?> findDeviceAndFloorPlanByFId(
    		HttpServletRequest req,
    		@RequestParam("Fid") String Fid) {
        Result<DeviceFloor> result = new Result<>();
        DeviceFloor deviceFloor = devicePlaceService.findDeviceAndFloorPlanByFId(Integer.parseInt(Fid.trim()),
        		super.getLoginUser(req).getId());
        result.setResult(deviceFloor);
        return result;
    }



    /**
     * 接收图标xy位置，保存起来
     * @param xyAxisDTO
     * @return
     */
    @PostMapping("saveDeviceXY")
    @ApiOperation(value = "接收图标xy位置，保存起来", notes = "接收图标xy位置，保存起来")
    public Result<?> saveDeviceXY(@RequestBody XyAxisDTO xyAxisDTO) {
        if(xyAxisDTO.getId()!=null){
//            System.out.println("==================================变更后的xy坐标"+xyAxisDTO);
            if(xyAxisDTO.getXaxis()=="null"){
                xyAxisDTO.setXaxis(null);
                xyAxisDTO.setYaxis(null);
            }
            deviceService.saveDeviceXY(xyAxisDTO);
        }
        return Result.ok();
    }
    /**
     * 跟据设备类型查询设备图标信息
     * @param type
     * @return
     */
    @GetMapping("getPicByDeviceType")
    @ApiOperation(value = "跟据设备类型查询设备图标信息", notes = "跟据设备类型查询设备图标信息")
    public Result<?> getPicByDeviceType(@RequestParam("type")  String type) {

        Result<List<DeviceIcon>> result = new Result<>();
        List<DeviceIcon> deviceIcons= deviceIconService.getPicByDeviceType(type);
        result.setResult(deviceIcons);
        return result;
    }
    
    /**
     * 根据客户id查询设备信息
     * @param Fid
     * @return
     */
    @GetMapping("findDeviceByCid")
    @ApiOperation(value = "根据客户id查询设备信息", notes = "根据客户id查询设备信息")
    public Result<?> findDeviceByCid(HttpServletRequest req, @RequestParam("Fid") Integer Fid) {
        Result<List<DeviceDTO>> result = new Result<>();
        //查询楼层信息，查询楼层信息根据客户id
        FloorPlan floorPlan = floorPlanService.getById(Fid);
        List<DeviceDTO> deviceDTOS = deviceService.findDeviceByCid(
        		floorPlan.getCustomerId(),
        		super.getLoginUser(req).getId());
        		/*findDeviceByCid(floorPlan.getCustomerId());*/
        result.setResult(deviceDTOS);
        return result;
    }


    @PostMapping("saveDeviceFloor")
    @ApiOperation(value = "保存设备楼层", notes = "保存设备楼层")
    public Result<?> saveDeviceFloor(@RequestBody DeviceXY deviceXY) {

        if (deviceXY != null) {
            Boolean b=  deviceService.saveDeviceFloor(deviceXY);
            if (b) {
                return Result.ok();
            }
        }
        return Result.error("添加失败");

    }

}
