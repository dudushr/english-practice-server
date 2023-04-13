package com.dudu.english.utils;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
	private static PropertiesUtils propUtils;
	Properties props;
	
	public static PropertiesUtils getInstance() {
		if(propUtils == null) {
			propUtils = new PropertiesUtils();
		}
		
		return propUtils;
	}
	
	private PropertiesUtils() {
		try {
			String resourceName = "resources/properties/english.properties"; // could also be a constant
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			props = new Properties();
			try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
			    props.load(resourceStream);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String get(String key) {
		return props.getProperty(key);
	}
}
