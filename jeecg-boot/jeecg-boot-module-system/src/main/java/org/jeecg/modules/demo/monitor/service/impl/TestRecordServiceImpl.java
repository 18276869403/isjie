package org.jeecg.modules.demo.monitor.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.demo.monitor.entity.Device;
import org.jeecg.modules.demo.monitor.entity.DeviceIcon;
import org.jeecg.modules.demo.monitor.entity.TestRecord;
import org.jeecg.modules.demo.monitor.mapper.DeviceIconMapper;
import org.jeecg.modules.demo.monitor.mapper.DeviceMapper;
import org.jeecg.modules.demo.monitor.mapper.TestRecordMapper;
import org.jeecg.modules.demo.monitor.service.ITestRecordService;
import org.jeecg.modules.demo.monitor.util.SattusSplitUtil;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;
import org.jeecg.modules.demo.monitor.vo.DetectorDTO;
import org.jeecg.modules.demo.monitor.vo.DetectorDTOout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 监测记录表
 * @Author: jeecg-boot
 * @Date:   2019-12-20
 * @Version: V1.0
 */
@Service
public class TestRecordServiceImpl extends ServiceImpl<TestRecordMapper, TestRecord> implements ITestRecordService {
	@Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceIconMapper deviceIconMapper;


    private static Map<String,String> map;

    static{
        map = new HashMap<>();
        map.put("1","探测器");
        map.put("2","控制器");
        map.put("3","输出模块");
    }


    @Override
    public DetectorDTOout queryFloorDetector(DetectorDTO detectorDTO) {
        DetectorDTOout detectorDTOout = new DetectorDTOout();
        Device device  =  deviceMapper.selectById(detectorDTO.getId());//deviceMapper.queryDeviceByImeiAndIccid(detectorDTO);
        String testPv1 = device.getBackup2();
        String time    = device.getBackup1();
        detectorDTOout.setAcquisitionTime(time);
        
        if(StringUtils.isNotBlank(testPv1)){
            DecimalFormat df  = new DecimalFormat("###.#######");
            BigDecimal testPv = new BigDecimal(device.getBackup2());
            testPv = testPv.multiply(device.getReductionCoefficient());
            String textPv = df.format(testPv) + device.getUnit();
            detectorDTOout.setTestPv(textPv);
        }
        String deviceType = map.get(device.getDeviceType()); //设备类型

        List<DeviceIcon> picByDeviceType = deviceIconMapper.getPicByDeviceType(deviceType);

        detectorDTOout.setStatusType(device.getStatusType());
        detectorDTOout.setUnit(device.getUnit());

        String status = SattusSplitUtil.getStatus4(device.getStatusType());//设备状态
        for (DeviceIcon deviceIcon : picByDeviceType) {
            if (status.equals(deviceIcon.getIconType())) {
                detectorDTOout.setPicUrl(deviceIcon.getIconPic());
                break;
            }
        }
        return detectorDTOout;
    }

    @Override
    public int addTest(List<CaiJiVO> caiJiVO) {
        return testRecordMapper.addTest(caiJiVO);
    }
}
