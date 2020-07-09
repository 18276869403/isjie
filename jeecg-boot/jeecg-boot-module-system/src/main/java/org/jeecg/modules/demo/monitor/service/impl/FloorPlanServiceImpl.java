package org.jeecg.modules.demo.monitor.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jeecg.modules.demo.monitor.entity.DeviceIcon;
import org.jeecg.modules.demo.monitor.entity.FloorPlan;
import org.jeecg.modules.demo.monitor.mapper.DeviceIconMapper;
import org.jeecg.modules.demo.monitor.mapper.DeviceMapper;
import org.jeecg.modules.demo.monitor.mapper.FloorPlanMapper;
import org.jeecg.modules.demo.monitor.service.IFloorPlanService;
import org.jeecg.modules.demo.monitor.service.ITestRecordService;
import org.jeecg.modules.demo.monitor.util.SattusSplitUtil;
import org.jeecg.modules.demo.monitor.vo.DeviceInfoDTO;
import org.jeecg.modules.demo.monitor.vo.FloorPlanDTO;
import org.jeecg.modules.demo.monitor.vo.FlorVo;
import org.jeecg.modules.demo.monitor.vo.XyAxisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 楼层图表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Service
@Transactional
public class FloorPlanServiceImpl extends ServiceImpl<FloorPlanMapper, FloorPlan> implements IFloorPlanService {
	@Autowired
	private FloorPlanMapper floorPlanMapper;
	@Autowired
	private DeviceMapper deviceMapper;
	@Autowired
	private DeviceIconMapper deviceIconMapper;
	@Autowired
	private ITestRecordService testRecordService;

	private static Map<String,String> map;

	static{
		map = new HashMap<>();
		map.put("1","探测器");
		map.put("2","控制器");
		map.put("3","输出模块");
	}


	@Override
	public IPage<FlorVo> custoMpage(Page<FlorVo> page, QueryWrapper<FlorVo> queryWrapper) {
		return floorPlanMapper.custoMpage(page, queryWrapper);
		
	}

	public IPage<FlorVo> webselectFlor(Page<FlorVo> page, QueryWrapper<FlorVo> queryWrapper) {
		return floorPlanMapper.custoMpage(page, queryWrapper);
		
	}

    @Override
    public List<FloorPlanDTO> queryFloorNameAndIdByCid(Integer cid, String userId) {
		List<FloorPlanDTO> floorPlanById = floorPlanMapper.queryFloorPlan(cid, userId);

		return floorPlanById;
    }

    @Override
    public List<DeviceInfoDTO> queryFloorDeviceByFid(Integer fid, String userId) {
		//根据楼层id查询设备ids
		List<Integer> ids = floorPlanMapper.findDeviceId(fid, userId);
		if (ids.size() > 0) {
			//根据设备ids查询设备的集合
			List<DeviceInfoDTO> deviceInfoDTOS = deviceMapper.queryFloorDeviceByFid(ids);
			//取出里面的   查询主设备id，在根据id查询出量程，单位，型号，imei
			for (DeviceInfoDTO deviceInfoDTO : deviceInfoDTOS) {
				deviceInfoDTO.setTestPv(deviceInfoDTO.getTestPv() + deviceInfoDTO.getUnit());
				//将设备的类型转化为汉字（1，探测器；2，控制器；3，输出模块）
				deviceInfoDTO.setDeviceType(map.get(deviceInfoDTO.getDeviceType()));
				//将设备的状态转变为5种状态，来取设备对应每种状态的图标
				String statusType = deviceInfoDTO.getStatusType();
//				deviceInfoDTO.setStatusType(SattusSplitUtil.getStatus(statusType));
				//取图标
				List<DeviceIcon> deviceIcons = deviceIconMapper.getPicByDeviceType(deviceInfoDTO.getDeviceType());
				//判断是哪一种图标
				for (DeviceIcon deviceIcon : deviceIcons) {
					if (deviceIcon.getIconType().equals(SattusSplitUtil.getStatus4(statusType))) {
						deviceInfoDTO.setPicUrl(deviceIcon.getIconPic());
						break;
					}
				}
				//拿到设备保存的位置
				XyAxisDTO xyAxisDTO = deviceMapper.findDeviceXYByDidAndFid(deviceInfoDTO.getId(), fid);
				deviceInfoDTO.setXposition(xyAxisDTO.getXaxis());
				deviceInfoDTO.setYposition(xyAxisDTO.getYaxis());

			}
			return deviceInfoDTOS;
		}

		return null;
    }


	@Override
	public void saveFloorAndDevice(FloorPlan floorPlan) throws Exception{
//		List<Integer> ids=deviceMapper.findDeviceIdsByCid(floorPlan.getCustomerId());
//		//根据设备ids查询设备信息
//		if(ids.size()<=0){
//			return;
//		}
//		List<DeviceDTO> deviceByCid = deviceMapper.findDeviceByIdS(ids);
//		for (DeviceDTO device: deviceByCid) {
//			// 查找设备是否已经绑定过
//			List<Integer> fIds = this.deviceMapper.findFloorByDeviceId(device.getId());
//			if(fIds != null && fIds.size() > 0) {
//				throw new Exception(device.getDeviceImei() + "+" + 
//							device.getDeviceNum() + "+" +
//							device.getDevicePositionNum() + "设备已绑定过楼层");
//			}
//		}
//		for (DeviceDTO device: deviceByCid) {
//			XyAxisDTO deviceXYByDidAndFid = deviceMapper.findDeviceXYByDidAndFid(device.getId(), floorPlan.getId());
//			if(deviceXYByDidAndFid == null){
//				DeviceXY deviceXY = new DeviceXY();
//				deviceXY.setCid(floorPlan.getCustomerId());
//				deviceXY.setDid(device.getId());
//				deviceXY.setFid(floorPlan.getId());
//				deviceMapper.saveDeviceXYByDidFid(deviceXY);
//			}
//		}
	}

	@Override
	public void deletefloordevice(String id) {
		floorPlanMapper.deletefloordevice(id);
	}
}
