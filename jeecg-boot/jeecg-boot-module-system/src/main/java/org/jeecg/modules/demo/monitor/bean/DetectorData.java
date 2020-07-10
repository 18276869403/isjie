package org.jeecg.modules.demo.monitor.bean;

public class DetectorData {
	private String ip;
	private String com;
	private String type;
	private String data;
	private Integer startAddress;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCom() {
		return com;
	}
	public void setCom(String com) {
		this.com = com;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Integer getStartAddress() {
		return startAddress;
	}
	public void setStartAddress(Integer startAddress) {
		this.startAddress = startAddress;
	}
}
