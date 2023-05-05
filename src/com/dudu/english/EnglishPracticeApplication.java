package com.dudu.english;
import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackages = "com.dudu.english")
@Configuration
@EnableWebMvc
public class EnglishPracticeApplication {

	
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
		app.setDefaultProperties(Collections.singletonMap("server.port", port));
		app.run(args);
		System.out.println("=======================================================");
		System.out.println("			Happy Coding");
		System.out.println("=======================================================");
	}
	
	

}
