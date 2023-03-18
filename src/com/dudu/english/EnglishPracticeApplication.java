package com.dudu.english;
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
		SpringApplication.run(EnglishPracticeApplication.class, args);
		
		System.out.println("=======================================================");
		System.out.println("			Happy Coding");
		System.out.println("=======================================================");
	}
	
	

}
