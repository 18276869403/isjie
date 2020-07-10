package org.jeecg.modules.demo.monitor.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jeecg.modules.demo.monitor.entity.Area;
import org.jeecg.modules.demo.monitor.entity.DeviceArea;
import org.jeecg.modules.demo.monitor.mapper.AreaMapper;
import org.jeecg.modules.demo.monitor.mapper.CustomersMapper;
import org.jeecg.modules.demo.monitor.mapper.DeviceAreaMapper;
import org.jeecg.modules.demo.monitor.service.IAreaService;
import org.jeecg.modules.demo.monitor.vo.AreaDTO;
import org.jeecg.modules.demo.monitor.vo.AreaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 省市表
 * @Author: jeecg-boot
 * @Date:   2020-01-03
 * @Version: V1.0
 */
@Service
@Transactional
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {

	@Autowired
	private CustomersMapper custMapper;
	@Autowired
	private AreaMapper areaMapper;
	@Autowired
	private DeviceAreaMapper deviceAreaMapper;
	
	//分配查询
	@Override
	public List<Map<String, Object>> findOrgUserTree(int num) {
		//查找根节点
		List<Map<String, Object>> list = areaMapper.findListByNum(num);
		List<Map<String, Object>> children;
		for (Map<String, Object> map : list) {

			children = findOrgUserTree((int)map.get("id"));

			map.put("title", map.get("label"));
			map.put("key", map.get("id").toString());

			if (children != null && children.size() != 0) {
				map.put("children", children);

			} else {
				children = custMapper.findCutByAreaId((int)map.get("id"));
				for (Map<String, Object> child : children) {
					child.put("title", child.get("label"));
					child.put("key", child.get("id").toString());
					// 查客户区域
					QueryWrapper<DeviceArea> query = new QueryWrapper<DeviceArea>();
					query.eq("customer_id", child.get("id"));
					List<DeviceArea> areas = this.deviceAreaMapper.selectList(query);
					if(areas != null && areas.size() > 0) {
						List<Map<String, Object>> areaChild = new ArrayList<Map<String,Object>>();
						for(DeviceArea da : areas) {
							Map<String, Object> c = new HashMap<String, Object>();
							c.put("title", da.getAreaName());
							c.put("key", da.getId());
							areaChild.add(c);
						}
						child.put("children", areaChild);
					}
				}

				if (children != null && children.size() != 0) {
					map.put("children", children);
				}
				//设置叶子组织机构（没有客户），为不可选
                if(children == null || children.size() == 0){
                    map.put("disabled",true);
                }
			}
		}
		return list;
	}

	//城市列表查询
	@Override
	public IPage<AreaVo> queryAreaVoAll(Page<AreaVo> page, Wrapper<AreaVo> wrapper) {
		// TODO Auto-generated method stub
		return areaMapper.queryAreaVoAll(page, wrapper);
	}

	//删除省份时删除城市
	@Override
	public boolean deleteOne(String id) {
		//删除省份
		this.removeById(id);
		//批量删除对应城市
		LambdaQueryWrapper<Area> queryWrapper = new LambdaQueryWrapper<Area>();
		queryWrapper.eq(Area::getNum, id);
		areaMapper.delete(queryWrapper);
		return false;
	}

	@Override
	public Boolean updateCustomersArea(AreaDTO areaDTO) {
		String useId = areaDTO.getUseId();
		List<Long> cid = areaDTO.getKeys();
		int i= areaMapper.deleteAreaAll(useId);

		if (i >= 0) {
			for (Long id : cid) {
				areaMapper.addAll(useId, id);
			}
			return true;
		}
		return false;

	}

	@Override
	public List<Long> findCustomerByUid(String uid) {
		List<Long> list=areaMapper.findCustomerByUid(uid);

		return list;
	}

	@Override
	public List<Integer> findAllAid() {
		 List<Integer> clist=areaMapper.findAllCid();
		 List<Integer> alist=areaMapper.findAllAid();
		 alist.addAll(clist);
		return alist;

	}

}
