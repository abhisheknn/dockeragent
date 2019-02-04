package com.micro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.support.WebApplicationContextUtils;

@SpringBootApplication
@EnableScheduling
public class App {
	
	public static void main(String[] args) {
		
		SpringApplication.run(App.class, args);
	}
}
