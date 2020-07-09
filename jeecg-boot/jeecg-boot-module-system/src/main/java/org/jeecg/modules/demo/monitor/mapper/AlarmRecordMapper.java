package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.AlarmRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.demo.monitor.vo.AlarmDiferentVo;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;

/**
 * @Description: 报警记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface AlarmRecordMapper extends BaseMapper<AlarmRecord> {

    /**
     * 查询警报数量
     * @param cid
     *
     * @return
     */
    List<AlarmDiferentVo> queryAlarmCountByCid(@Param("cid") Integer cid);



    /**
     * 根据设备ids和设备类型查询 设备报警数量
     * @param ids
     * @param type
     * @param status
     * @return
     */
    int queryAlarmCountByCidnew(@Param("ids") List<Integer> ids, @Param("type") String type, @Param("status") String status);

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
