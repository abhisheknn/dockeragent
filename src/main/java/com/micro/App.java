package com.micro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.micro.client.mqtt.MQTTConnector;
import com.micro.client.publish.Publish;
import com.micro.policy.PolicyEnforcer;

@SpringBootApplication
@EnableScheduling
public class App {
	@Autowired
	MQTTConnector mqttConnector;
	
	@Autowired
	Publish publish;
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
