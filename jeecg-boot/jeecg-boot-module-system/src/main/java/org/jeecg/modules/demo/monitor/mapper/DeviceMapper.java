package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.jeecg.modules.demo.monitor.vo.DetectorDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceInfoDTO;
import org.jeecg.modules.demo.monitor.vo.DeviceVo;
import org.jeecg.modules.demo.monitor.vo.DeviceXY;
import org.jeecg.modules.demo.monitor.vo.XyAxisDTO;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date: 2019-12-20
 * @Version: V1.0
 */
public interface DeviceMapper extends BaseMapper<Device> {
	IPage<DeviceVo> custoMpage(Page<DeviceVo> page, @Param(Constants.WRAPPER) QueryWrapper<DeviceVo> queryWrapper);

	List<DeviceVo> exportXls(@Param(Constants.WRAPPER) QueryWrapper<DeviceVo> queryWrapper);

	/**
	 * @param ids 根据设备ids查询设备信息
	 * @return
	 */
	List<DeviceDTO> findDeviceByIdS(@Param("ids") List<Integer> ids);

	/**
	 * 根据设备id，楼层id，查询设备的xy坐标
	 *
	 * @param dId
	 * @param fid
	 * @return
	 */
	XyAxisDTO findDeviceXYByDidAndFid(@Param("dId") Integer dId, @Param("fid") Integer fid);

	/**
	 * @param xyAxisDTO 保存设备坐标
	 */
	void saveDeviceXY(XyAxisDTO xyAxisDTO);

	List<Integer> findDeviceIdsByCid(@Param("id") Integer customerId,
			@Param("userId") String userId);

	

	int saveDeviceXYByDidFid(DeviceXY deviceXY);

	// 根据id查设备的数量
	int queryDeviceById(@Param("id") Integer id);
	//前端实时数据查询设备总数
	public int selectDeviceCount(@Param("customerId") int customerId);

	int selectCountDeviceByFlor(@Param("customerId") int customerId, @Param("floorId") int floorId);

	List<Integer> selectDeviceIds(@Param("customerId") int customerId, @Param("floorId") int floorId);

	/**
	 * 根据楼层id查询该楼层的设备详情
	 * @param ids
	 * @return
	 */
	List<DeviceInfoDTO> queryFloorDeviceByFid(@Param("ids") List<Integer> ids);

	/**
	 * 根据客户id查询所有的设备ids
	 * @param customerId
	 * @return
	 */
	List<Integer> queryDeviceIdsByCid(@Param("customerId") int customerId);

	/**
	 *根据设备ids查询设备状态的集合
	 * @param ids
	 * @return
	 */
	List<String> queryDeviceStatusTypeByDids(@Param("ids") List<Integer> ids);

	/**
	 * 根据设备ids和设备类型查询设备类型数量
	 * @param ids
	 * @param type
	 * @return
	 */
	int queryDeviceTypeCountByDid(@Param("ids") List<Integer> ids, @Param("type") String type);

	List<Map<String, Integer>> findDtypeCountByDid(@Param("ids") List<Integer> ids);

	/**
	 * 根据设备ids查询所有的数量
	 * @param ids
	 * @return
	 */
	int queryDeviceCount(@Param("ids") List<Integer> ids);

	Device queryDeviceByImeiAndIccid(DetectorDTO detectorDTO);

    List<Integer> findFloorByDeviceId(@Param("did") Integer id);

    //设备导出根据客户名查询客户id
	List<DeviceVo> findDeviceCust(@Param("cname")String cname);

    int updateByImeiAndAdd(CaiJiVO caiJiVO);
    
    int syncRealDataById(List<CaiJiVO> caiJiVO);

	Device findDeviceByImeiAndAdd(CaiJiVO caiJiVO);

    void deletefloordevice(@Param("id") String id);

	List<Device> queryAlarmdevice(@Param("userId") String userId,@Param("customerId") Integer customerId);

    void deleteByCustomerId(@Param("customerId")String id);

    List<Device> selectDeviceByNoStatus(@Param("status")String status);

	DeviceVo findDeviceCustByNamePreject(@Param("name")String name, @Param("project")String project);
	
	List<Device> groupByImei();

	int updateHeatTime(CaiJiVO cv);
	
	int updateHeatTimeStatus(Device device);
}
