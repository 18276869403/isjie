package org.jeecg.modules.demo.monitor.service;


import org.jeecg.modules.demo.monitor.vo.TestRecordVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITestRecordVoService   extends IService<TestRecordVo> {

	
    //查询前段实时记录表
	public IPage<TestRecordVo> selectTestAll(Page<TestRecordVo> page, Wrapper<TestRecordVo> wrapper);
	
	// 查询所有监测表
	public IPage<TestRecordVo> queryTestAll(Page<TestRecordVo> page, Wrapper<TestRecordVo> wrapper);

}
