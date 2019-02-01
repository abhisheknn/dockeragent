package com.micro.client.restclient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Component;

import com.micro.client.publish.common.Constants;
import com.micro.constant.AppConstants;

@Component
public class RestClient extends com.micro.client.RestClient {
public RestClient() {
	Map<String, String> mandatoryHeaders= new HashMap<>();
	mandatoryHeaders.put(AppConstants.JWTOKEN, this.add());
	this.setMandatoryHeaders(mandatoryHeaders);
}

public String add() {
	String restEndpoint = Constants.HTTP+Constants.microRestEndpoint+"/auth/register";
	Map<String, Object> requestBody = new HashMap<>();
	Map<String, String> headers= new HashMap<>();
	requestBody.put(Constants.MACADDRESS, Constants.MACADDRESSFROMENV);
	try {
		return this.doPost(restEndpoint, requestBody,null);
	} catch (ClientProtocolException e) {
		e.printStackTrace();
	} catch (UnknownHostException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	return "";
}

}

