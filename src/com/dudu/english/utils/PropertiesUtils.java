package com.dudu.english.utils;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

public class PropertiesUtils {
	private static PropertiesUtils propUtils;
	Properties props;
	String hostAddress = "";
	
	public static PropertiesUtils getInstance() {
		if(propUtils == null) {
			propUtils = new PropertiesUtils();
		}
		
		return propUtils;
	}
	
	private PropertiesUtils() {
		try {
			this.hostAddress = getCurrentHostAddress();
			
			String resourceName = "resources/properties/english.properties"; // could also be a constant
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			
			props = new Properties();
			try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
			    props.load(resourceStream);
			}catch(Exception ex2) {
				ex2.printStackTrace();
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String get(String key) {
		String prefix = "";
		if("141.136.36.155".equals(hostAddress)) {
			prefix = "prod.";
		}else {
			prefix = "dev.";
		}
		
		return props.getProperty(prefix + key);
	}
	
	public String getPathDelim() {
		String delim = "";
		if("141.136.36.155".equals(hostAddress)) {
			delim = "/";
		}else {
			delim = "\\";
		}
		
		return delim;
	}
	
	
	public String getCurrentHostName() {
        try {
            InetAddress localMachine = InetAddress.getLocalHost();
            String hostname = localMachine.getHostName();
            
            return hostname;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "localhost";
        }
    }
	
	public String getCurrentHostAddress() {
        try {
            InetAddress localMachine = InetAddress.getLocalHost();
            String hostAddress = localMachine.getHostAddress();
                        
            return hostAddress;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "localhost";
        }
    }
}
