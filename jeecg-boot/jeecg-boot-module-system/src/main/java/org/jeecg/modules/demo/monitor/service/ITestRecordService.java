package org.jeecg.modules.demo.monitor.service;

import java.util.List;

import org.jeecg.modules.demo.monitor.entity.TestRecord;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.jeecg.modules.demo.monitor.vo.DetectorDTO;
import org.jeecg.modules.demo.monitor.vo.DetectorDTOout;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 监测记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
public interface ITestRecordService extends IService<TestRecord> {
	
    DetectorDTOout queryFloorDetector(DetectorDTO detectorDTO);

    int addTest(List<CaiJiVO> caiJiVO);
}
