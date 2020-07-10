package org.jeecg.modules.demo.monitor.controller;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.monitor.bean.DetectorData;
import org.jeecg.modules.demo.monitor.controller.async.AsyncProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "采集接口")
@RestController
@EnableAsync
@RequestMapping("/monitor/caiji")
public class ComRecordController {
    @Autowired
    private RedisUtil redis;
    @Autowired
    private AsyncProcess asyncProcess;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Object setStationLastDataTime(String key, Object date) {
    	return this.redis.set("device#lastDataTime#station#" + key, date);
    }
//    //@AutoLog(value = "采集数据-COM类型添加")
    @ApiOperation(value = "服务采集数据-添加", notes = "服务采集数据-添加")
    @PostMapping(value = "/putComData")
    public Result<?> putComData(@RequestBody List<DetectorData> dataList){
    	if(dataList == null || dataList.size() == 0) return Result.ok("无数据");
    	Date now = new Date();
    	log.info("开始采集COM设备数据：" + JSON.toJSONString(dataList));
    	// 实时更新心跳数据
    	if(StringUtils.isNotEmpty(dataList.get(0).getData())) {
    		this.setStationLastDataTime("0" + dataList.get(0).getData().substring(0, 2), dateFormat.format(now));
    	}
    	asyncProcess.asyncPutComData(dataList, now.getTime());
//    	applicationContext.getBean(ComRecordController.class).asyncPutComData(data, now.getTime());
//    	this.putComData(caiJiVOs, data.getType());
    	return Result.ok("添加成功");
    }
}
