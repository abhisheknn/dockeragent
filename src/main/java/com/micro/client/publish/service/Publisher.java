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

import com.micro.client.RestClient;
import com.micro.client.publish.common.PUBLISHTYPE;
import com.micro.client.publish.common.Constants;

@Component
public class Publisher {
	

	public void publish(PUBLISHTYPE type, String key, Object value) {
		String restEndpoint = Constants.HTTP+Constants.microRestEndpoint+"/publish/docker?hostname="+key;
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put(Constants.KEY, key);
		requestBody.put(Constants.VALUE, value);
		requestBody.put(Constants.TYPE, type.name());
		try {
			Map<String, String> requestHeaders= new HashMap<>();
			requestHeaders.put("Content-Type", ContentType.APPLICATION_JSON.toString());
			RestClient.doPost(restEndpoint, requestBody,requestHeaders);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
