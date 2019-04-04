package com.micro.client.mqtt;

import java.util.Optional;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.micro.client.publish.common.Constants;
import com.micro.policy.PolicyEnforcer;
import com.mirco.mqtt.MQTTAsync;

@Component
public class MQTTConnector {

	@Autowired
	PolicyEnforcer policyEnforcer;

	private Optional<IMqttAsyncClient> mqttAsyncClient;

	public Optional<IMqttAsyncClient> getMqttAsyncClient() {
		return mqttAsyncClient;
	}

	public MQTTConnector() {
		mqttAsyncClient = MQTTAsync.getClient("tcp://" + Constants.MQTTBROKER, Constants.CLIENTIDSUBSCRIBER+2);
		mqttAsyncClient.ifPresent((subscriber) -> {

			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(false);
			options.setKeepAliveInterval(1000000000);
			options.setConnectionTimeout(1000);
			try {
				MQTTAsync.connect(options, subscriber, null, new IMqttActionListener() {

					@Override
					public void onSuccess(IMqttToken var1) {
						MQTTAsync.subscribe(subscriber, Constants.MQTTTOPIC, 2, (m, msg) -> {
							synchronized (subscriber) {
								policyEnforcer.applyPolicy(new String(msg.getPayload()));
							}
						});
					}

					@Override
					public void onFailure(IMqttToken var1, Throwable var2) {
						var2.printStackTrace();
					}
				});
			} catch (MqttSecurityException e) {
				e.printStackTrace();
			} catch (MqttException e) {
				e.printStackTrace();
				System.exit(0);
			}

		});
	}
}
