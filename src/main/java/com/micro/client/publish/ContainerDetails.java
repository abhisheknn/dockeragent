package com.micro.client.publish;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.micro.client.RestClient;
import com.micro.client.publish.common.Constants;
import com.micro.client.publish.common.PUBLISHTYPE;
//import com.micro.client.publish.common.Constants;
//import com.micro.client.publish.common.PUBLISHTYPE;
import com.micro.client.publish.service.Publisher;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

@Component
public class ContainerDetails implements Publish {
	@Autowired
	private Publisher publisher;
	private Gson gson = new Gson();
	
	@Override
	@Scheduled(fixedRate = 500)
	public boolean send() {
		
		try {
			final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");
			List<Container> containers = docker.listContainers();
			publisher.publish(PUBLISHTYPE.CONTAINERINFO,Constants.hostName, gson.toJson(containers));
			docker.close();
			} catch (DockerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
}
