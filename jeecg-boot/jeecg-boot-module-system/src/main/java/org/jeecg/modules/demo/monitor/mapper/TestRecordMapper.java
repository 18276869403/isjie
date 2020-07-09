package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.entity.TestRecord;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.jeecg.modules.demo.monitor.vo.DetectorDTO;
import org.jeecg.modules.demo.monitor.vo.DetectorDTOout;
import org.jeecg.modules.demo.monitor.vo.TestRecordVo;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 监测记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface TestRecordMapper extends BaseMapper<TestRecord> {

	/**
     * 根据设备查询检测记录
     * @param detectorDTO
     * @return
     */
    TestRecordVo queryFloorDetector(DetectorDTO detectorDTO);

    int addTest(List<CaiJiVO> caiJiVO);
}
