package org.jeecg.modules.demo.monitor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceTypeConf {

	private static String type;
	@Value("${collection.type}")
	public void setType(String type) {
		DeviceTypeConf.type = type;
	}
	private static final String TYPE_4G  = "4G";
	private static final String TYPE_COM = "COM";
	
	public static String getType() {
		return type;
	}
	public static boolean is4G() {
		return TYPE_4G.equals(type);
	}
	public static boolean isCom() {
		return TYPE_COM.equals(type);
	}
}
