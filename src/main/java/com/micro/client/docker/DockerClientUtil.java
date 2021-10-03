package com.micro.client.docker;

import org.springframework.stereotype.Component;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;

@Component
public class DockerClientUtil {
	private DockerClientConfig config ;
	private DockerCmdExecFactory dockerCmdExecFactory;
	private DockerClient dockerClient;
	public DockerClientUtil() {

		config=DefaultDockerClientConfig.createDefaultConfigBuilder()
			  .withDockerHost("unix:///var/run/docker.sock")
			  //.withDockerHost("tcp://localhost:2375")
			  .build();

		 dockerCmdExecFactory	= new JerseyDockerCmdExecFactory()
			 // .withReadTimeout(1000)
			  //.withConnectTimeout(1000)
			  .withMaxTotalConnections(100)
			  .withMaxPerRouteConnections(10);
	}

	public DockerClient getClient() {
		if(dockerClient==null) {
		dockerClient = DockerClientBuilder.getInstance(config)
				  .withDockerCmdExecFactory(dockerCmdExecFactory)
				  .build();
		}
		return dockerClient;
	}
}
