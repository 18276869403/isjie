package org.jeecg.modules.demo.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.monitor.vo.TestRecordForPageVO;
import org.jeecg.modules.demo.monitor.vo.TestRecordVo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TestRecordVoMapper extends BaseMapper<TestRecordVo>{
	
	// 查询所有监测表
	public  IPage<TestRecordVo> queryTestAll(Page<TestRecordVo> page, @Param(Constants.WRAPPER) Wrapper<TestRecordVo> wrapper);
	
	//查询前段实时记录表
	public  IPage<TestRecordVo> selectTestAll(Page<TestRecordVo> page, @Param(Constants.WRAPPER) Wrapper<TestRecordVo> wrapper);
	
	public List<TestRecordVo> exportXlsTestAll(@Param(Constants.WRAPPER) Wrapper<TestRecordVo> wrapper);
	//分页查询实时记录
    List<TestRecordVo> queryTestByPage(TestRecordForPageVO testRecordForPageVO);

	public TestRecordVo queryTestByPage1(int tid);

	Integer findCount(TestRecordForPageVO testRecordForPageVO);

	List<TestRecordVo> queryTestByPage3(TestRecordForPageVO testRecordForPageVO);
}
