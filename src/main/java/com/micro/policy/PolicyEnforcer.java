package com.micro.policy;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.micro.client.mqtt.MQTTConnector;
import com.micro.client.publish.common.Constants;
import com.mirco.mqtt.MQTTAsync;

@Component
@ConditionalOnProperty(prefix = "c2", name = "enabled", havingValue = "true")
public class PolicyEnforcer {
	Gson gson= new GsonBuilder().disableHtmlEscaping().create();

	public void applyPolicy(String payload) {
		Policy policy=gson.fromJson(payload, Policy.class);
		applyRules(policy.getRules());
		storePolicy(policy);
	}

	private void storePolicy(Policy policy) {
		// TODO Auto-generated method stub

	}

	private void applyRules(List<Map<String, String>> rules) {
	for(Map<String, String> rule : rules) {
			String ruleExpression=rule.get("ruleExpression");
			System.out.println(ruleExpression);
	}
	}

}
