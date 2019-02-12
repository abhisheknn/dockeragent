package com.micro.client.publish.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.micro.client.publish.common.PUBLISHTYPE;
import com.micro.client.restclient.RestClient;
import com.micro.client.publish.common.Constants;

@Component
public class Publisher {
	
	@Autowired
	RestClient restClient;
	
	
	public void publish(PUBLISHTYPE type, String key, Object value) {
		String restEndpoint = Constants.HTTP+Constants.microRestEndpoint+"/publish/docker?macaddress="+key;
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put(Constants.KEY, key);
		requestBody.put(Constants.VALUE, value);
		requestBody.put(Constants.TYPE, type.name());
		System.out.println(requestBody);
		try {
			Map<String, String> requestHeaders= new HashMap<>();
			requestHeaders.put("Content-Type","application/json");
			restClient.doPost(restEndpoint, requestBody,requestHeaders);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
