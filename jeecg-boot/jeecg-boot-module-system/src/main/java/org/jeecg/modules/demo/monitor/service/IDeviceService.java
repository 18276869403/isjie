package org.jeecg.modules.demo.monitor.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.vo.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface IDeviceService extends IService<Device> {
	IPage<DeviceVo> custoMpage(Page<DeviceVo> page, @Param(Constants.WRAPPER) QueryWrapper<DeviceVo> queryWrapper);
	void saveDeviceXY(XyAxisDTO xyAxisDTO);
    List<DeviceDTO> findDeviceByCid(Integer customerId, String userId);

	Boolean saveDeviceFloor(DeviceXY deviceXY);
	//前端实时数据查询设备总数
	DeviceCountDTO selectDeviceCount(@Param("customerId") int customerId);


	//前端实时数据根据楼层查询设备总数
	DeviceCountDTO selectCountDeviceByFlor(@Param("customerId") int customerId, @Param("floorId") int floorId);

    void saveFloorDevice(Device device);

    DeviceCountDTO queryDeviceCountByUid(String uid);

	DeviceTypeCount queryDeviceTypeCountByCid(Integer cid, String userId);

//根据用户id查询设备类型数量
	DeviceTypeCount queryDeviceTypeCountByUid(String uid);

    List<Integer> findFloorByDeviceId(Integer id);

//    int updateByImeiAndAdd(CaiJiVO caiJiVO);
    /**
     * 同步设备状态
     * @param caiJiVO
     * @return
     */
    int syncRealDataById(List<CaiJiVO> caiJiVO);

	Device findDeviceByImeiAndAdd(CaiJiVO caiJiVO);

    void deletefloordevice(String id);

	List<Device> queryAlarmdevice(String userId, Integer customerId);

    List<Device> selectDeviceByNoStatus(String status);
    
    List<Device> groupByImei();
    
	void updateHeatTime(CaiJiVO cv);
	
	int updateHeatTimeStatus(Device device);
}
