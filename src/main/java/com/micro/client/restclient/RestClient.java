package com.micro.client.restclient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.micro.client.Response;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.micro.client.publish.common.Constants;
import com.micro.constant.AppConstants;

@Component
public class RestClient extends com.micro.client.RestClient {
	Gson gson = new Gson();

	public RestClient() {
		Map<String, String> mandatoryHeaders = new HashMap<>();
		mandatoryHeaders.put(AppConstants.JWTOKEN, this.add());
		mandatoryHeaders.put(Constants.MACADDRESS,Constants.MACADDRESSFROMENV);
		mandatoryHeaders.put(Constants.TENANTIDSTR,Constants.TENANTIDFROMENV);
		this.setMandatoryHeaders(mandatoryHeaders);
		}

	public String add() {
		String restEndpoint = Constants.HTTP + Constants.microRestEndpoint+ "/auth/register";
		Response response = null;
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put(Constants.MAC_ADDRESS, Constants.MACADDRESSFROMENV);
		requestBody.put(Constants.TENANT_ID, Constants.TENANTIDFROMENV);
		try {
			Map<String, String> requestHeaders = new HashMap<>();
			requestHeaders.put("Content-Type", "application/json");
			response = super.doPost(restEndpoint, requestBody, requestHeaders);
			JsonParser parser = new JsonParser();
			JsonObject jsonObject = parser.parse(response.getStatusLine()).getAsJsonObject();
			JsonElement jsonElement = jsonObject.get("statusCode");
			if (jsonElement.getAsInt() == HttpStatus.SC_NOT_FOUND) {
				System.exit(0);
			}

		} catch (ClientProtocolException e) {
			System.out.println(e);
		} catch (UnknownHostException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		return response.getEntity();
	}
}
