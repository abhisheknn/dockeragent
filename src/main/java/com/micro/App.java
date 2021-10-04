package com.micro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.micro.client.mqtt.MQTTConnector;
import com.micro.client.publish.Publish;
import com.micro.policy.PolicyEnforcer;

@SpringBootApplication
@EnableScheduling
@ComponentScan
public class App {
  public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
