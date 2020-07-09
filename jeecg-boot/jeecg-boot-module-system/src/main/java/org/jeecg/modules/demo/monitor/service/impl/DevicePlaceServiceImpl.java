package org.jeecg.modules.demo.monitor.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jeecg.modules.demo.monitor.entity.DeviceIcon;
import org.jeecg.modules.demo.monitor.entity.FloorPlan;
import org.jeecg.modules.demo.monitor.mapper.CustomersMapper;
import org.jeecg.modules.demo.monitor.mapper.DeviceIconMapper;
import org.jeecg.modules.demo.monitor.mapper.DeviceMapper;
import org.jeecg.modules.demo.monitor.mapper.FloorPlanMapper;
import org.jeecg.modules.demo.monitor.service.IDevicePlaceService;
import org.jeecg.modules.demo.monitor.util.SattusSplitUtil;
import org.jeecg.modules.demo.monitor.vo.DeviceDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceFloor;
import org.jeecg.modules.demo.monitor.vo.DevicePlace;
import org.jeecg.modules.demo.monitor.vo.FloorPlanDTO;
import org.jeecg.modules.demo.monitor.vo.XyAxisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class DevicePlaceServiceImpl extends ServiceImpl<FloorPlanMapper, FloorPlan> implements IDevicePlaceService {


    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceIconMapper deviceIconMapper;
    @Autowired
    private FloorPlanMapper floorPlanMapper;
    @Autowired
    private CustomersMapper customersMapper;

    private static Map<String,String> map;

    static{
        map = new HashMap<>();
        map.put("1","探测器");
        map.put("2","控制器");
        map.put("3","输出模块");
    }

    @Override
    public DevicePlace findDevicePlaceByFloorPlanId(Integer id,
    		Integer Fid, String userId) {

        DevicePlace devicePlace = new DevicePlace();
        //根据客户id查询楼层信息
        List<FloorPlanDTO> floorPlanDTOS=floorPlanMapper.findFloorPlanById(id);
        List<DeviceDTO> deviceDTOS = getDeviceDTOS(Fid, userId);


        devicePlace.setFloorPlanDTOS(floorPlanDTOS);
        devicePlace.setDeviceDTOS(deviceDTOS);
        //返回信息
        return devicePlace;
    }



    @Override
    public DeviceFloor findDeviceAndFloorPlanByFId(Integer fid, String userId) {
        DeviceFloor deviceFloor = new DeviceFloor();
        //根据楼层id查询楼层信息
        FloorPlanDTO floorPlanDTO= floorPlanMapper.findFloorPlanByFId(fid);
        List<DeviceDTO> deviceDTOS = getDeviceDTOS(fid, userId);
        deviceFloor.setDeviceDTOS(deviceDTOS);
        deviceFloor.setFloorPlanDTO(floorPlanDTO);
        return deviceFloor;
    }

    //根据楼层id查询该楼层设备信息
    private List<DeviceDTO> getDeviceDTOS(Integer Fid, String userId) {
        //根据楼层Fid查询设备ids
        List<Integer> ids = floorPlanMapper.findDeviceId(Fid, userId);
        System.out.println("设备id： "+ids);
        //根据设备ids查询设备信息集合
        List<DeviceDTO> deviceDTOS = new ArrayList<DeviceDTO>();
        if(ids.size()!=0){
            deviceDTOS= deviceMapper.findDeviceByIdS(ids);
            for (DeviceDTO deviceDTO : deviceDTOS) {
                deviceDTO.setDeviceType(map.get(deviceDTO.getDeviceType()));
                deviceDTO.setStatusType(SattusSplitUtil.getStatus(deviceDTO.getStatusType()));
                DeviceIcon deviceIcons = new DeviceIcon();
                if(deviceDTO.getStatusType().indexOf("警")>=0){
                    deviceIcons=deviceIconMapper.getPicByDeviceTypeAndStatus(deviceDTO.getDeviceType(),"报警");
                }else{
                    deviceIcons=deviceIconMapper.getPicByDeviceTypeAndStatus(deviceDTO.getDeviceType(),deviceDTO.getStatusType());
                }
                deviceDTO.setPicUrl(deviceIcons.getIconPic());
                //根据设备id和楼层Fid查询设备的xy轴
                Integer dId = deviceDTO.getId();
                XyAxisDTO xyAxisDTO= deviceMapper.findDeviceXYByDidAndFid(dId, Fid);
                deviceDTO.setXyAxisDTO(xyAxisDTO);
                deviceDTO.setXposition(xyAxisDTO.getXaxis());
                deviceDTO.setYposition(xyAxisDTO.getYaxis());
            }
        }

        return deviceDTOS;
    }

    @Override
    public List<FloorPlan> findFloorByDeviceId(Integer did,Integer cid) {
        List<FloorPlan> floorPlanList =  floorPlanMapper.findFloorByDeviceId(did,cid);
        return floorPlanList;
    }
}
