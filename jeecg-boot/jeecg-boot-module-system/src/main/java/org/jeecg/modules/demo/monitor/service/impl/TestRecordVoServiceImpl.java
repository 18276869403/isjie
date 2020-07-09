package org.jeecg.modules.demo.monitor.service.impl;


import org.jeecg.modules.demo.monitor.mapper.TestRecordVoMapper;

import org.jeecg.modules.demo.monitor.service.ITestRecordVoService;
import org.jeecg.modules.demo.monitor.vo.TestRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class TestRecordVoServiceImpl  extends ServiceImpl<TestRecordVoMapper,TestRecordVo> implements ITestRecordVoService  {

//测试service实现层
	@Autowired
	private TestRecordVoMapper testRecordVoMapper;
	
	

	@Override
	public IPage<TestRecordVo> selectTestAll(Page<TestRecordVo> page,Wrapper<TestRecordVo>wrapper){
		return testRecordVoMapper.selectTestAll(page, wrapper);
		
	}



	@Override
	public IPage<TestRecordVo> queryTestAll(Page<TestRecordVo> page, Wrapper<TestRecordVo> wrapper) {
		return testRecordVoMapper.queryTestAll(page, wrapper);
	}
}
