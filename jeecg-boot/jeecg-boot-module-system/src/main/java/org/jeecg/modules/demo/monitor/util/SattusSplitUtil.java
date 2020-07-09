package org.jeecg.modules.demo.monitor.util;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.demo.monitor.vo.CaiJiVO;

public class SattusSplitUtil {

	public static String getStatus(String status) {
		if (status == null) {
			return "正常";
		}
		if (status.indexOf("正常") >= 0) {
			return "正常";
		}
		if (status.indexOf("高警") >= 0) {
			return "高警";
		}
		if (status.indexOf("低警") >= 0) {
			return "低警";
		}
		if (status.indexOf("通讯故障") >= 0) {
			return "通讯故障";
		}
		if (status.indexOf("故障") >= 0) {
			return "故障";
		}
		if (status.indexOf("主电欠压") >= 0) {
			return "故障";
		}
		if (status.indexOf("备电欠压") >= 0) {
			return "故障";
		}
		if (status.indexOf("维护") >= 0) {
			return "维护";
		}
		return "故障";
	}

	public static String getStatus4(String status) {
		if (status == null) {
			return "正常";
		}
		if (status.indexOf("正常") >= 0) {
			return "正常";
		}
		if (status.indexOf("高警") >= 0) {
			return "报警";
		}
		if (status.indexOf("低警") >= 0) {
			return "报警";
		}
		if (status.indexOf("通讯故障") >= 0) {
			return "通讯故障";
		}
		if (status.indexOf("故障") >= 0) {
			return "故障";
		}
		if (status.indexOf("主电欠压") >= 0) {
			return "故障";
		}
		if (status.indexOf("备电欠压") >= 0) {
			return "故障";
		}
		if (status.indexOf("维护") >= 0) {
			return "维护";
		}
		return null;
	}

	public static String getStatus1(CaiJiVO caiji) {
		String status = caiji.getStatus();
//        if(status == null){
//            return "正常";
//        }
//        if(status.indexOf("通讯故障")>=0){
//            return "通讯故障";
//        }
//        if(status.indexOf("正常")>=0){
//            return "正常";
//        }
//        if(status.indexOf("高警")>=0){
//            return "发生高警";
//        }
//        if(status.indexOf("低警")>=0){
//            return "发生低警";
//        }
//        return "发生故障";
		StringBuilder res = new StringBuilder();
		if (StringUtils.isNotBlank(status)) {
			String ra[] = status.split(",");
			for (String r : ra) {
				res.append("发生").append(r).append(",");
			}
			res = res.deleteCharAt(res.length() - 1);
		}
		// 如果为输出模块需加上pv变化记录
		if(caiji.getDevice() != null 
				&& "3".equals(caiji.getDevice().getDeviceType())) {
			if(StringUtils.isNotBlank(caiji.getPv())) {
				try {
					if(Integer.parseInt(caiji.getPv()) == 0) {
						res.append("（未动作）");
					} else {
						res.append("（已动作）");
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return res.toString();
	}

	public static String getStatus2(CaiJiVO caiji) {
		String status = caiji.getStatus();
//        if(status == null){
//            return "正常";
//        }
//        if(status.indexOf("正常")>=0){
//            return "正常";
//        }
//        if(status.indexOf("高警")>=0){
//            return "解除高警";
//        }
//        if(status.indexOf("低警")>=0){
//            return "解除低警";
//        }
//        if(status.indexOf("通讯")>=0){
//            return "通讯恢复";
//        }
//        return "解除故障";
		StringBuilder res = new StringBuilder();
		if (StringUtils.isNotBlank(status)) {
			String ra[] = status.split(",");
			for (String r : ra) {
				res.append("解除").append(r).append(",");
			}
			res = res.deleteCharAt(res.length() - 1);
		}
		// 如果为输出模块需加上pv变化记录
		if(caiji.getDevice() != null 
				&& "3".equals(caiji.getDevice().getDeviceType())) {
			if(StringUtils.isNotBlank(caiji.getPv())) {
				try {
					if(Integer.parseInt(caiji.getPv()) == 0) {
						res.append("（未动作）");
					} else {
						res.append("（已动作）");
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return res.toString();
	}
}
