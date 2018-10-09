package com.micro.client.publish.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.micro.client.RestClient;
import com.micro.client.publish.common.PUBLISHTYPE;
import com.micro.client.publish.common.Constants;

@Component
public class Publisher {
	

	public void publish(PUBLISHTYPE type, String key, String value) {
		String restEndpoint = Constants.HTTP+Constants.microRestEndpoint+"/publish/docker?hostname="+key;
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put(Constants.KEY, key);
		requestBody.put(Constants.VALUE, value);
		requestBody.put(Constants.TYPE, PUBLISHTYPE.PERFORMANCEMETRIC.name());
		try {
			RestClient.doPost(restEndpoint, requestBody, null);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
