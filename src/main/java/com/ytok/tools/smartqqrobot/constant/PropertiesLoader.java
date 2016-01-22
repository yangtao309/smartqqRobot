package com.ytok.tools.smartqqrobot.constant;


import java.io.IOException;
import java.util.Properties;

/**
 * 配置加载类
 */
public class PropertiesLoader {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PropertiesLoader.class);
	private static Properties properties = null;

	private static Properties getInterfaceProperties() {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(PropertiesLoader.class.getClassLoader().getResourceAsStream("config.properties"));
			} catch (IOException e) {
				logger.error("加载config.properties出错！");
			}
		}
		return properties;
	}

	public static String getValue(String key) {
		return getInterfaceProperties().getProperty(key);
	}

	/** 图灵帐号的apiKey **/
	public static final String API_KEY = getInterfaceProperties().getProperty("apiKey");

	/** 企业QQ face **/
	public static final Integer FACE = Integer.parseInt(getInterfaceProperties().getProperty("face"));

}