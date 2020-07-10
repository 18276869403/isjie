package org.jeecg.modules.demo.monitor.service;

import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.monitor.vo.AlarmDiferentVo;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.jeecg.modules.demo.monitor.vo.DeviceTypeCount;

import java.util.List;
import java.util.Map;

/**
 * @Description: 报警记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface IAlarmRecordService extends IService<AlarmRecord> {



    DeviceTypeCount queryAlarmCountByCidnew(Integer cid, String userId);

//    根据用户id查询设备类型及所属的警报数量
    DeviceTypeCount queryAlarmCountByUid(String uid);

    int insertAlarm(AlarmRecord alarmRecord);

    AlarmRecord selectByImeiAndAdd(CaiJiVO caiJiVO);

    void deleteAll();
    
    /**
     * @param caiJiVO
     * @return
     */
    Map<String, Object> selectConnectionError(CaiJiVO caiJiVO);
    
    /**
     * @param caiJiVO
     * @return
     */
    int updateConnectionRecovery(CaiJiVO caiJiVO);
}
