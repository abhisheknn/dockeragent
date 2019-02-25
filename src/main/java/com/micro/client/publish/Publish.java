package com.micro.client.publish;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.ChangeLog;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.EventActor;
import com.github.dockerjava.api.model.EventType;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.core.command.EventsResultCallback;
import com.micro.client.docker.Collect;
import com.micro.client.docker.DockerClientUtil;
import com.micro.client.publish.common.Constants;
import com.micro.client.publish.common.PUBLISHTYPE;

import com.micro.client.publish.service.Publisher;

@Component
public class Publish {

	@Autowired
	private Publisher publisher;

	@Autowired
	private DockerClientUtil dockerClientUtil;

	@Autowired
	private Collect collect;
	
	@PostConstruct
	public void send() {
		publisher.publish(PUBLISHTYPE.NETWORK_LIST, Constants.MACADDRESSFROMENV, collect.network());
		publisher.publish(PUBLISHTYPE.CONTAINERLIST, Constants.MACADDRESSFROMENV, collect.containers());	
		publisher.publish(PUBLISHTYPE.VOLUMELIST, Constants.MACADDRESSFROMENV, collect.volumes());
		
		EventsResultCallback callback = new EventsResultCallback() {
			@Override
			public void onNext(Event event) {
				if (event.getType() == EventType.CONTAINER) collectContainer(event);
				if (event.getType() == EventType.NETWORK) collectNetwork(event);
				if (event.getType() == EventType.VOLUME)  collectVolume(event);
				super.onNext(event);
			}

			private void collectContainer(Event event) {
					
					String containerId = event.getId();
					List<String> ids = Arrays.asList(containerId);
					List<Container> containers = dockerClientUtil.getClient().listContainersCmd().withIdFilter(ids)
							.exec();
					
					if (event.getAction().equals(Constants.STARTACTION)) {
						publisher.publish(PUBLISHTYPE.CONTAINERINFO, Constants.MACADDRESSFROMENV, containers.get(0));
						
					}
					if (event.getAction().equals(Constants.STOPACTION)) {
						publisher.publish(PUBLISHTYPE.DELETEDCONTAINERS, Constants.MACADDRESSFROMENV, ids.get(0));
					}
					collect(containerId);
				
			}

			private void collectNetwork(Event event) {
				
				String networkId = event.getActor().getId();
					Network network=null;
					try {
					network=dockerClientUtil.getClient().inspectNetworkCmd().withNetworkId(networkId).exec();
					}catch(Exception e) {
						System.out.println(e);
					}
					
					if (event.getAction().equals(Constants.REMOVE)) {
						publisher.publish(PUBLISHTYPE.NETWORK_REMOVE, Constants.MACADDRESSFROMENV, network);
					}
					
					if (event.getAction().equals(Constants.CREATE)) {
						publisher.publish(PUBLISHTYPE.NETWORK_CREATE, Constants.MACADDRESSFROMENV, network);
					}
					else if (event.getAction().equals(Constants.CONNECT)) {
						publisher.publish(PUBLISHTYPE.NETWORK_CONNECT, Constants.MACADDRESSFROMENV, network);
					}
					else if (event.getAction().equals(Constants.DESTROY)) {
						publisher.publish(PUBLISHTYPE.NETWORK_DESTROY, Constants.MACADDRESSFROMENV, networkId);
					}
					if (event.getAction().equals(Constants.DISCONNECT)) {
						publisher.publish(PUBLISHTYPE.NETWORK_DISCONNECT, Constants.MACADDRESSFROMENV, network);
					}
			}
			
			
			private void collectVolume(Event event) {
					String volumeId = event.getActor().getId();
					
					
					if (event.getAction().equals(Constants.CREATE)) {
						publisher.publish(PUBLISHTYPE.VOLUME_CREATE, Constants.MACADDRESSFROMENV, event.getActor());
					}
					else if (event.getAction().equals(Constants.MOUNT)) {
						publisher.publish(PUBLISHTYPE.VOLUME_MOUNT, Constants.MACADDRESSFROMENV, event.getActor());
					}
					else if (event.getAction().equals(Constants.DESTROY)) {
						publisher.publish(PUBLISHTYPE.VOLUME_DESTROY, Constants.MACADDRESSFROMENV, volumeId);
					}
					if (event.getAction().equals(Constants.UNMOUNT)) {
						publisher.publish(PUBLISHTYPE.VOLUME_UNMOUNT, Constants.MACADDRESSFROMENV, event.getActor());
					}
			}
		};
		
		try {
			dockerClientUtil.getClient().eventsCmd().exec(callback).awaitCompletion();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Scheduled(fixedRate=300000)     // Can be set by server
	private void collect(String containerId) {
		try {
		String[][] processes=collect.process(containerId);
		
		publisher.publish(PUBLISHTYPE.PROCESSES, Constants.MACADDRESSFROMENV+"_"+containerId,processes);
		List<ChangeLog> fileDiff=collect.fileDiff(containerId);
		publisher.publish(PUBLISHTYPE.FILE_DIFF, Constants.MACADDRESSFROMENV+"_"+containerId,fileDiff);
		boolean present= fileDiff.stream().filter(k->k.getPath().equals(Constants.ROOT_BASH_HISTORY)).findFirst().isPresent();
		if(present) {List<String> commands = collect.command(containerId);
		publisher.publish(PUBLISHTYPE.COMMANDS, Constants.MACADDRESSFROMENV+"_"+containerId,commands);
		}
		
		}catch(Exception e){
			System.out.println(e);
		}
		
		
		

	}
}
