package com.micro.client.publish.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.micro.client.RestClient;
import com.micro.client.publish.common.PUBLISHTYPE;
import com.micro.client.publish.common.Constants;

@Component
public class Publisher {
	private static final String KEY = "key";
	private static final String TYPE = "TYPE";
	private static final String VALUE = "value";
	private static final String HOSTNAME = "hostname";
	private static final String CONTAINER_ID = "containerID";

	public void publish(PUBLISHTYPE type, String key, String value) {
		String restEndpoint = Constants.microRestEndpoint+"/publish/docker";
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put(KEY, key);
		requestBody.put(VALUE, value);
		requestBody.put(TYPE, PUBLISHTYPE.PERFORMANCEMETRIC.name());
		RestClient.doPost(restEndpoint, requestBody, null);
	}
}
