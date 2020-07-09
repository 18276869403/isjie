package org.jeecg.modules.demo.monitor.service.impl;

import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import org.jeecg.modules.demo.monitor.mapper.AlarmRecordMapper;
import org.jeecg.modules.demo.monitor.mapper.CustomersMapper;
import org.jeecg.modules.demo.monitor.mapper.DeviceMapper;
import org.jeecg.modules.demo.monitor.service.IAlarmRecordService;
import org.jeecg.modules.demo.monitor.vo.AlarmDiferentVo;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.jeecg.modules.demo.monitor.vo.DeviceTypeCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 报警记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Service
public class AlarmRecordServiceImpl extends ServiceImpl<AlarmRecordMapper, AlarmRecord> implements IAlarmRecordService {
    @Autowired
    private AlarmRecordMapper alarmRecordMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private CustomersMapper customersMapper;



    DeviceTypeCount typeCount = new DeviceTypeCount();


    /**
     * 根据客户id查询设备类型及所属的警报数量
     * @param cid
     * @return
     */
    @Override
    public DeviceTypeCount queryAlarmCountByCidnew(Integer cid, String userId) {
//        DeviceTypeCount typeCount = new DeviceTypeCount();
        Integer detectorCount = 0;
        Integer controllerCount = 0;
        Integer outCount = 0;
        String status = "%警%";
        //根据cid查询所有的设备ids
        List<Integer> ids = deviceMapper.findDeviceIdsByCid(cid, userId);
        //根据ids,设备类型 查询所有的报警设备
        if (ids.size() > 0) {
            detectorCount =     queryDTypeCount(ids, "1",status);
            controllerCount =   queryDTypeCount(ids, "2",status);
            outCount =          queryDTypeCount(ids, "3",status);
        }
        typeCount.setDetectorCount(detectorCount);
        typeCount.setControllerCount(controllerCount);
        typeCount.setOutCount(outCount);
        typeCount.setCount(detectorCount + controllerCount + outCount);

        return typeCount;
    }

    private int queryDTypeCount(List<Integer> ids, String type,String status) {
       return alarmRecordMapper.queryAlarmCountByCidnew(ids,type,status);
    }

    /**
     * 根据用户id查询设备类型及所属的警报数量
     * @param uid
     * @return
     */
    @Override
    public DeviceTypeCount queryAlarmCountByUid(String uid) {
        //根据用户查询所有的客户
        List<Integer> cids = customersMapper.queryCustomerIdByUid(uid);
//        DeviceTypeCount typeCount = new DeviceTypeCount();
        Integer detectorCount = 0;
        Integer controllerCount = 0;
        Integer outCount = 0;
        if (cids.size() > 0) {
            for (Integer id : cids) {
            	if(id == null) {
            		continue;
            	}
                //判断每一个客户下是否有设备
                List<Integer> clist = deviceMapper.queryDeviceIdsByCid(id);
                if (clist.size() > 0) {
                    DeviceTypeCount deviceTypeCount = queryAlarmCountByCidnew(id, uid);
                    detectorCount += deviceTypeCount.getDetectorCount();
                    controllerCount += deviceTypeCount.getControllerCount();
                    outCount += deviceTypeCount.getOutCount();
                }
            }
        }

        typeCount.setDetectorCount(detectorCount);
        typeCount.setControllerCount(controllerCount);
        typeCount.setOutCount(outCount);
        typeCount.setCount(detectorCount+controllerCount+outCount);
        return typeCount;
    }

    @Override
    public int insertAlarm(AlarmRecord alarmRecord) {
        return alarmRecordMapper.insertAlarm(alarmRecord);
    }

    @Override
    public AlarmRecord selectByImeiAndAdd(CaiJiVO caiJiVO) {
        return alarmRecordMapper.selectByImeiAndAdd(caiJiVO);
    }

    @Override
    public void deleteAll() {
        alarmRecordMapper.deleteAll();
    }


    
    /**
     * @param caiJiVO
     * @return
     */
    public Map<String, Object> selectConnectionError(CaiJiVO caiJiVO) {
    	return this.alarmRecordMapper.selectConnectionError(caiJiVO);
    }
    
    /**
     * @param caiJiVO
     * @return
     */
    public int updateConnectionRecovery(CaiJiVO caiJiVO) {
    	return this.alarmRecordMapper.updateConnectionRecovery(caiJiVO);
    }
}
