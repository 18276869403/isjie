package org.jeecg.modules.demo.monitor.vo;

import lombok.Data;
import org.jeecg.modules.demo.monitor.vo.DeviceDTODetailIn;

import java.util.List;


@Data
public class DeviceDTOIn  {
    private List<DeviceDTODetailIn> deviceDTODetailIns;
}
