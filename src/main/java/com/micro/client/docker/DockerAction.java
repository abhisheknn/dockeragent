package com.micro.client.docker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class DockerAction {
@Autowired
DockerClientUtil dockerClientUtil;
	
	public void killContainer(String containerId) {
		dockerClientUtil.getClient().killContainerCmd(containerId).exec();
	}
}
