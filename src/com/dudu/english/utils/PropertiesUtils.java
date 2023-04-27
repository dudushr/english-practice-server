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
			System.out.println("-----> loader = " + loader);
			props = new Properties();
			try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
			    props.load(resourceStream);
			}catch(Exception ex2) {
				ex2.printStackTrace();
			}
			System.out.println("----> props was loaded " + props);
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
		System.out.println("-----> prefix = " + prefix);
		
		return props.getProperty(prefix + key);
	}
	
	
	public String getCurrentHostName() {
        try {
            InetAddress localMachine = InetAddress.getLocalHost();
            String hostname = localMachine.getHostName();
            System.out.println("Machine hostname: " + hostname);
            
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
            System.out.println("Machine address: " + hostAddress);
            
            return hostAddress;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "localhost";
        }
    }
}
