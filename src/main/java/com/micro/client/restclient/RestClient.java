package com.micro.client.restclient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.protocol.HTTP;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.micro.client.publish.common.Constants;
import com.micro.constant.AppConstants;

@Component
public class RestClient extends com.micro.client.RestClient {
	Gson gson= new Gson();
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
		String res=this.doPost(restEndpoint, requestBody,null);
		JsonParser parser = new JsonParser();
		JsonObject jsonObject= parser.parse(res).getAsJsonObject();
		JsonElement jsonElement=jsonObject.get("statusCode");
		if(jsonElement.getAsInt()==HttpStatus.SC_NOT_FOUND) {
			System.exit(0);
		}
	} catch (ClientProtocolException e) {
		System.out.println(e);
	} catch (UnknownHostException e) {
		System.out.println( e);
	} catch (IOException e) {
		System.out.println(e);
	}
	return "";
}

}

