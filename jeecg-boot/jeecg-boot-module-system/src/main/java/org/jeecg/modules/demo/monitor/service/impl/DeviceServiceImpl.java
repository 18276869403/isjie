package org.jeecg.modules.demo.monitor.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.monitor.entity.Customers;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.mapper.CustomersMapper;
import org.jeecg.modules.demo.monitor.mapper.DeviceMapper;
import org.jeecg.modules.demo.monitor.mapper.FloorPlanMapper;
import org.jeecg.modules.demo.monitor.service.IDeviceService;
import org.jeecg.modules.demo.monitor.util.SattusSplitUtil;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.jeecg.modules.demo.monitor.vo.DeviceCountDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceTypeCount;
import org.jeecg.modules.demo.monitor.vo.DeviceVo;
import org.jeecg.modules.demo.monitor.vo.DeviceXY;
import org.jeecg.modules.demo.monitor.vo.FloorPlanDTO;
import org.jeecg.modules.demo.monitor.vo.XyAxisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date: 2019-12-20
 * @Version: V1.0
 */
@Service
@Transactional
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private FloorPlanMapper floorPlanMapper;
    @Autowired
    private CustomersMapper customersMapper;
    @Autowired
    private CustomersServiceImpl customersService;
    @Autowired
    private RedisUtil redis;

    private DeviceTypeCount deviceTypeCount = new DeviceTypeCount();
    private DeviceCountDTO deviceCountDTO = new DeviceCountDTO();


    private static Map<String, String> map;

    static {
        map = new HashMap<>();
        map.put("1", "探测器");
        map.put("2", "控制器");
        map.put("3", "输出模块");
    }


    @Override
    public IPage<DeviceVo> custoMpage(Page<DeviceVo> page, QueryWrapper<DeviceVo> queryWrapper) {
        return deviceMapper.custoMpage(page, queryWrapper);
    }

    @Override
    public void saveDeviceXY(XyAxisDTO xyAxisDTO) {
        //保存设备xy
        deviceMapper.saveDeviceXY(xyAxisDTO);
    }

    //根据客户id查询所有设备
    @Override
    public List<DeviceDTO> findDeviceByCid(Integer customerId, String userId) {
        List<Integer> ids = deviceMapper.findDeviceIdsByCid(customerId, userId);
        //根据设备ids查询设备信息
        List<DeviceDTO> deviceDTOS = deviceMapper.findDeviceByIdS(ids);
        for (DeviceDTO deviceDTO : deviceDTOS) {
            deviceDTO.setDeviceType(map.get(deviceDTO.getDeviceType()));
            deviceDTO.setStatusType(SattusSplitUtil.getStatus(deviceDTO.getStatusType()));
        }
        return deviceDTOS;
    }

    @Override
    public Boolean saveDeviceFloor(DeviceXY deviceXY) {
        XyAxisDTO xyAxisDTO = deviceMapper.findDeviceXYByDidAndFid(deviceXY.getDid(), deviceXY.getFid());
        if (xyAxisDTO != null) {
            xyAxisDTO.setXaxis(deviceXY.getXaxis());
            xyAxisDTO.setYaxis(deviceXY.getYaxis());
            // 修改
            deviceMapper.saveDeviceXY(xyAxisDTO);
            return true;
        }
        int i = deviceMapper.saveDeviceXYByDidFid(deviceXY);

        return i == 1;

    }

    /**
     * 根据用户id查询该公司所有的设备数量
     *
     * @param customerId
     * @return
     */
    @Override
    public DeviceCountDTO selectDeviceCount(int customerId) {

		DeviceCountDTO deviceCountDTO = new DeviceCountDTO();
        //正常设备数量
        int deviceNormalCount = 0;
        //维护
        int deviceDefendCount = 0;
        //故障
        int deviceFaultCount = 0;
        //通讯故障
        int deviceCommFailCount = 0;
        //报警设备
        int deviceAlertCount = 0;
        //总设备数量
        int deviceCount = 0;
                //todo ===============
        //根据客户id查询所有的设备ids集合
        List<Integer> ids = deviceMapper.queryDeviceIdsByCid(customerId);
        if(ids.size()>0){
            //根据设备ids查询所有的状态集合
            List<String> list = deviceMapper.queryDeviceStatusTypeByDids(ids);
            //总设备数量
            deviceCount = deviceMapper.selectDeviceCount(customerId);
            //判断总设备是否大于0
            if (deviceCount > 0) {
                for (String s : list) {
                    if (StringUtils.contains(s, "正常")) {
                        deviceNormalCount += 1;
                    } else if (StringUtils.contains(s, "通讯故障")) {
                        deviceCommFailCount += 1;
                    } else if (StringUtils.contains(s, "高警")) {
                        deviceAlertCount += 1;
                    } else if (StringUtils.contains(s, "低警")) {
                        deviceAlertCount += 1;
                    } else if (StringUtils.contains(s, "故障")) {
                        deviceFaultCount += 1;
                    } else if (StringUtils.contains(s, "主电欠压")) {
                        deviceFaultCount += 1;
                    } else if (StringUtils.contains(s, "备电欠压")) {
                        deviceFaultCount += 1;
                    } else if (StringUtils.contains(s, "维护")) {
                        deviceDefendCount += 1;
                    }

                }
            }
        }
        //todo ===============


        deviceCountDTO.setDeviceCount(deviceCount);
        deviceCountDTO.setDeviceNormalCount(deviceNormalCount);
        deviceCountDTO.setDeviceFaultCount(deviceFaultCount);
        deviceCountDTO.setDeviceDefendCount(deviceDefendCount);
        deviceCountDTO.setDeviceCommFailCount(deviceCommFailCount);
        deviceCountDTO.setDeviceAlertCount(deviceAlertCount);

        return deviceCountDTO;


    }

    @Override
    public DeviceCountDTO selectCountDeviceByFlor(int customerId, int floorId) {

		DeviceCountDTO deviceCountDTO = new DeviceCountDTO();
        //正常设备数量
        int deviceNormalCount = 0;
        //维护
        int deviceDefendCount = 0;
        //故障
        int deviceFaultCount = 0;
        //通讯故障
        int deviceCommFailCount = 0;
        //报警设备
        int deviceAlertCount = 0;

        //每层楼的总数量
        int count = deviceMapper.selectCountDeviceByFlor(customerId, floorId);
        int qiyu = 0;
        if (count > 0) {
            //每层楼的设备id集合
            List<Integer> ids = deviceMapper.selectDeviceIds(customerId, floorId);
            if (ids.size() > 0) {
                //根据设备ids查询所有的状态集合
                List<String> list = deviceMapper.queryDeviceStatusTypeByDids(ids);

                //判断总设备是否大于0
                if (count > 0) {
                    for (String s : list) {
                        if (StringUtils.contains(s, "正常")) {
                            deviceNormalCount += 1;
                        } else if (StringUtils.contains(s, "通讯故障")) {
                            deviceCommFailCount += 1;
                        } else if (StringUtils.contains(s, "高警")) {
                            deviceAlertCount += 1;
                        } else if (StringUtils.contains(s, "低警")) {
                            deviceAlertCount += 1;
                        } else if (StringUtils.contains(s, "故障")) {
                            deviceFaultCount += 1;
                        } else if (StringUtils.contains(s, "主电欠压")) {
                            deviceFaultCount += 1;
                        } else if (StringUtils.contains(s, "备电欠压")) {
                            deviceFaultCount += 1;
                        } else if (StringUtils.contains(s, "维护")) {
                            deviceDefendCount += 1;
                        }else{
                            qiyu +=1;
                        }

                    }

                }
            }
        }

        deviceCountDTO.setDeviceCount(count);
        deviceCountDTO.setDeviceNormalCount(deviceNormalCount);
        deviceCountDTO.setDeviceFaultCount(deviceFaultCount);
        deviceCountDTO.setDeviceDefendCount(deviceDefendCount);
        deviceCountDTO.setDeviceCommFailCount(deviceCommFailCount);
        deviceCountDTO.setDeviceAlertCount(deviceAlertCount);
        System.out.println("=================================================================================");
        System.out.println(qiyu);
        System.out.println("=================================================================================");

        return deviceCountDTO;
    }

    @Override
    public void saveFloorDevice(Device device) {
//		List<Integer> ids=deviceMapper.findDeviceIdsByCid(device1.getCustomerId());
        //根据设备ids查询设备信息
//		if(ids.size()<=0){
//			return;
//		}
//		List<DeviceDTO> deviceByCid = deviceMapper.findDeviceByIdS(ids);
        //查出用户所有楼层
        List<FloorPlanDTO> floorPlanById = floorPlanMapper.findFloorPlanById(device.getCustomerId());
        if (floorPlanById.size() <= 0) {
            return;
        }
        for (FloorPlanDTO floorPlanDTO : floorPlanById) {
            XyAxisDTO deviceXYByDidAndFid = deviceMapper.findDeviceXYByDidAndFid(device.getId(), floorPlanDTO.getId());
            if (deviceXYByDidAndFid == null) {
                DeviceXY deviceXY = new DeviceXY();
                deviceXY.setCid(device.getCustomerId());
                deviceXY.setDid(device.getId());
                deviceXY.setFid(floorPlanDTO.getId());
                deviceMapper.saveDeviceXYByDidFid(deviceXY);
            }
        }
    }

    /**
     * 根据用户id查询总设备数量
     *
     * @param uid
     * @return
     */
    @Override
    public DeviceCountDTO queryDeviceCountByUid(String uid) {
        //根据用户id查询所有的客户
        List<Integer> list = customersMapper.queryCustomerIdByUid(uid);

        //根据客户id查询所有的类型数量相加
        int count = 0;                            //总数量
        int NormalCount = 0;            //正常数量
        int FaultCount = 0;                //故障数量
        int DefendCount = 0;            //维护数量
        int CommFailCount = 0;            //通讯故障数量
        int DeviceAlertCount = 0;        //报警数量

        if (list.size() > 0) {
            for (Integer cid : list) {
            	if(cid == null) {
            		continue;
            	}
                //判断每一个客户下是否有设备
                List<Integer> clist = deviceMapper.queryDeviceIdsByCid(cid);
                if (clist.size() > 0) {
                    //查询每一客户的数量
                    DeviceCountDTO deviceCountDTO = selectDeviceCount(cid);
                    count += deviceCountDTO.getDeviceCount();                    //总数量
                    NormalCount += deviceCountDTO.getDeviceNormalCount();                //正常数量
                    FaultCount += deviceCountDTO.getDeviceFaultCount();                //故障数量
                    DefendCount += deviceCountDTO.getDeviceDefendCount();                //维护数量
                    CommFailCount += deviceCountDTO.getDeviceCommFailCount();            //通讯故障数量
                    DeviceAlertCount += deviceCountDTO.getDeviceAlertCount();                //报警数量
                }

            }
        }


        deviceCountDTO.setDeviceCount(count);                    //总数量
        deviceCountDTO.setDeviceNormalCount(NormalCount);        //正常数量
        deviceCountDTO.setDeviceFaultCount(FaultCount);        //故障数量
        deviceCountDTO.setDeviceDefendCount(DefendCount);            //维护数量
        deviceCountDTO.setDeviceCommFailCount(CommFailCount);    //通讯故障数量
        deviceCountDTO.setDeviceAlertCount(DeviceAlertCount);    //报警数量


        return deviceCountDTO;
    }
    /**
     * 根据客户id查询设备类型数量
     *
     * @param cid
     * @return
     */
    @Override
    public DeviceTypeCount queryDeviceTypeCountByCid(Integer cid, String userId) {
//		DeviceTypeCount deviceTypeCount = new DeviceTypeCount();
        int count = 0;                    //总数量
        int detectorCount = 0;            //探测器
        int controllerCount = 0;        //控制器
        int outCount = 0;                //输出模块
        //根据客户id查询所有的设备id
        List<Integer> ids = deviceMapper.findDeviceIdsByCid(cid, userId);
        if (ids.size() > 0) {
            //根据设备id查询出所有的设备类型数量
            count = deviceMapper.queryDeviceCount(ids);
            detectorCount = queryDtypeCount(ids, "1");
            controllerCount = queryDtypeCount(ids, "2");
            outCount = queryDtypeCount(ids, "3");
        }
        deviceTypeCount.setCount(count);
        deviceTypeCount.setDetectorCount(detectorCount);
        deviceTypeCount.setControllerCount(controllerCount);
        deviceTypeCount.setOutCount(outCount);
        return deviceTypeCount;
    }

    @Override
    public DeviceTypeCount queryDeviceTypeCountByUid(String uid) {
        //根据用户id查询所有的客户
        List<Integer> cids = customersMapper.queryCustomerIdByUid(uid);
//		DeviceTypeCount deviceTypeCount = new DeviceTypeCount();
        int count = 0;                    //总数
        int detectorCount = 0;            //探测器
        int controllerCount = 0;        //控制器
        int outCount = 0;                //输出模块
        if (cids.size() > 0) {
            for (Integer id : cids) {
            	if(id == null) {
            		continue;
            	}
                //判断每一个客户下是否有设备
                List<Integer> clist = deviceMapper.queryDeviceIdsByCid(id);
                if (clist.size() > 0) {
                    //根据客户查询所有的类型数量
                    DeviceTypeCount countType = queryDeviceTypeCountByCid(id, uid);
                    System.out.println(countType);
                    count += countType.getCount();
                    detectorCount += countType.getDetectorCount();
                    controllerCount += countType.getControllerCount();
                    outCount += countType.getOutCount();
                }
            }
        }
        deviceTypeCount.setCount(count);
        deviceTypeCount.setDetectorCount(detectorCount);
        deviceTypeCount.setControllerCount(controllerCount);
        deviceTypeCount.setOutCount(outCount);


        return deviceTypeCount;
    }

    @Override
    public List<Integer> findFloorByDeviceId(Integer id) {
        return deviceMapper.findFloorByDeviceId(id);
    }

//    @Override
//    public int updateByImeiAndAdd(CaiJiVO caiJiVO) {
//        return deviceMapper.updateByImeiAndAdd(caiJiVO);
//    }

    @Override
    public Device findDeviceByImeiAndAdd(CaiJiVO caiJiVO) {
    	String key = "device#cache#" + caiJiVO.getImei() + 
    			"#" + caiJiVO.getIccid() + 
    			"#" + caiJiVO.getAdd();
    	Device device = (Device)this.redis.get(key);
    	if(device == null) {
			Date date = new Date();
    		device = deviceMapper.findDeviceByImeiAndAdd(caiJiVO);
    		log.debug("无缓存，数据库查找设备耗时：" + (new Date().getTime() - date.getTime()) + "毫秒");
    		if(device != null) {
    			date = new Date();
	    		Customers customer = this.customersService.getById(device.getCustomerId());
	    		log.debug("无缓存，数据库查找客户耗时：" + (new Date().getTime() - date.getTime()) + "毫秒");
	    		if(customer != null) {
		    		device.setOneAreaId(customer.getOneAreaId());
		    		device.setTwoAreaId(customer.getTwoAreaId());
	    		}
	    		date = new Date();
	    		this.redis.set(key, device);
	    		log.debug("设置缓存耗时：" + (date.getTime() - new Date().getTime()) + "毫秒");
    		}
    	} else {
			log.debug("缓存中的数据" + key);
    	}
        return device;
    }

    @Override
    public void deletefloordevice(String id) {
        deviceMapper.deletefloordevice(id);
    }

    @Override
    public List<Device> queryAlarmdevice(String userId, Integer customerId) {
        return deviceMapper.queryAlarmdevice(userId,customerId);
    }

    @Override
    public List<Device> selectDeviceByNoStatus(String status) {
        return deviceMapper.selectDeviceByNoStatus(status);
    }


    private int queryDtypeCount(List<Integer> ids, String type) {
        return deviceMapper.queryDeviceTypeCountByDid(ids, type);
    }

	@Override
	public List<Device> groupByImei() {
		return this.deviceMapper.groupByImei();
	}

	@Override
	public void updateHeatTime(CaiJiVO cv) {
		this.deviceMapper.updateHeatTime(cv);
	}

	@Override
	public int syncRealDataById(List<CaiJiVO> caiJiVO) {
		return this.deviceMapper.syncRealDataById(caiJiVO);
	}

	@Override
	public int updateHeatTimeStatus(Device device) {
		return this.deviceMapper.updateHeatTimeStatus(device);
	}
	
	public List<Device> list(){
		List<Device> cacheDevice = (List<Device>)this.redis.get("allDeviceCache");
		if(cacheDevice == null) {
			cacheDevice = super.list();
			this.redis.set("allDeviceCache", cacheDevice);
		}
		return cacheDevice;
	}

}
