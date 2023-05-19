package com.dudu.english;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = "com.dudu.english")
@Configuration
@EnableWebMvc
public class EnglishPracticeApplication implements WebMvcConfigurer{

	
	public static void main(String[] args) {
		String port = "8080";
		
		String currentDirectory = System.getProperty("user.dir");
		System.out.println("----> currentDirectory = " + currentDirectory);
		if(currentDirectory.indexOf("prod") != -1) {
			port = "8080";
		}else if(currentDirectory.indexOf("beta") != -1) {
			port = "8083";
		}
		System.out.println("----> Port = " + port);
		
		SpringApplication app = new SpringApplication(EnglishPracticeApplication.class);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("server.port", port);
		map.put("spring.servlet.multipart.max-file-size", "2MB");
		map.put("spring.servlet.multipart.max-request-size", "2MB");
		map.put("spring.resources.static-locations", "/resources/");
		
		app.setDefaultProperties(map);
		app.run(args);
		System.out.println("=======================================================");
		System.out.println("			Happy Coding");
		System.out.println("=======================================================");
	}
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200", "http://141.136.36.155:4200", "http://141.136.36.155:4210/")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
	
	

}
