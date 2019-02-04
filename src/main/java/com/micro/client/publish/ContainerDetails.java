package com.micro.client.publish;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.google.gson.Gson;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.micro.client.docker.DockerClientUtil;
import com.micro.client.publish.common.Constants;
import com.micro.client.publish.common.PUBLISHTYPE;
//import com.micro.client.publish.common.Constants;
//import com.micro.client.publish.common.PUBLISHTYPE;
import com.micro.client.publish.service.Publisher;
import com.micro.client.redis.RedisConnection;
//import com.spotify.docker.client.DefaultDockerClient;
//import com.spotify.docker.client.DockerClient;
//import com.spotify.docker.client.exceptions.DockerException;
//import com.spotify.docker.client.messages.Container;

@Component
public class ContainerDetails implements Publish {
	private static final String CONTAINERIDPREFIX = "containerid_"+Constants.hostName+"_";
	@Autowired
	private Publisher publisher;
	private Gson gson = new Gson();
	
	@Autowired
	private DockerClientUtil dockerClientUtil;
	@Autowired
	private RedisConnection redisConnnection;
	
	@Override
	@Scheduled(fixedRate = 5000)
	public boolean send() {
		
		try {
		 DockerClient dockerClient=dockerClientUtil.getClient();
		 List<Container>  containers= dockerClient.listContainersCmd().exec();
		 //dockerClient.close();
			List <String> deletedContainerIds=new ArrayList<>();
			Set<String> runningContainerSet= containers.stream()
		    		.map(Container::getId)
		    		.collect(Collectors.toSet());
			try (StatefulRedisConnection<String, String> connection = redisConnnection.getPool().borrowObject()) {
			    RedisCommands<String, String> commands = connection.sync();
			   
			    // This is to find out if you container got created after the last sync
			    containers= containers.stream()
			    		.filter(v->null==commands.get(CONTAINERIDPREFIX+v.getId()))
			    		.collect(Collectors.toList());
			    commands.multi();
			    // If new container got created put the 
			    // IDS of all newly created container in redis 
			    containers.stream()
						    .forEach((v)->{
						    	commands.set(CONTAINERIDPREFIX+v.getId(), v.getState());	
						    });
			    commands.exec();
			    // This is to find out deleted container after last sync
			    List<String> existingContainerIds=commands.keys(CONTAINERIDPREFIX+"*");
			    commands.multi();
			    deletedContainerIds= existingContainerIds.stream()
			    .filter(v->{  
			    	String key=v.replace(CONTAINERIDPREFIX,"");
			    	boolean isDeleted=!runningContainerSet.contains(key);
			    	if(isDeleted) {
			    		commands.del(v);
			    	}
			    	return isDeleted;
			    }).map((v)->{
			    	return v.replace(CONTAINERIDPREFIX,"");
			    })
			    .collect(Collectors.toList());
			    commands.exec();
			}
			if(!deletedContainerIds.isEmpty()) {
			publisher.publish(PUBLISHTYPE.DELETEDCONTAINERS,Constants.hostName, deletedContainerIds);
			}
			if(!containers.isEmpty()) {
			publisher.publish(PUBLISHTYPE.CONTAINERINFO,Constants.hostName, containers);
			}
			} catch (DockerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
