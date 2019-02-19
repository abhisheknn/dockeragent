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
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.ChangeLog;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.EventType;
import com.github.dockerjava.core.command.EventsResultCallback;
import com.micro.client.docker.Collect;
import com.micro.client.docker.DockerClientUtil;
import com.micro.client.publish.common.Constants;
import com.micro.client.publish.common.PUBLISHTYPE;

import com.micro.client.publish.service.Publisher;

@Component
public class ContainerDetails implements Publish {

	@Autowired
	private Publisher publisher;

	@Autowired
	private DockerClientUtil dockerClientUtil;

	@Autowired
	private Collect collect;
	
	@PostConstruct
	public void send() {
		EventsResultCallback callback = new EventsResultCallback() {
			@Override
			public void onNext(Event event) {
				if (event.getType() == EventType.CONTAINER) {
					String containerId = event.getId();
					List<String> ids = Arrays.asList(containerId);
					List<Container> containers = dockerClientUtil.getClient().listContainersCmd().withIdFilter(ids)
							.exec();
					if (event.getAction().equals(Constants.STARTACTION)) {
						publisher.publish(PUBLISHTYPE.CONTAINERINFO, Constants.MACADDRESSFROMENV, containers.get(0));
						collect(containerId);
					}
					if (event.getAction().equals(Constants.STOPACTION)) {
						publisher.publish(PUBLISHTYPE.DELETEDCONTAINERS, Constants.MACADDRESSFROMENV, ids.get(0));
						collect(containerId);
					}
				}
				super.onNext(event);
			}

			@Scheduled(fixedRate=300000)     // Can be set by server
			private void collect(String containerId) {
				try {
				String[][] processes=collect.process(containerId);
				publisher.publish(PUBLISHTYPE.PROCESSES, Constants.MACADDRESSFROMENV+"_"+containerId,processes);
				}catch(Exception e){
					System.out.println(e);
				}
				List<ChangeLog> fileDiff=collect.fileDiff(containerId);
				publisher.publish(PUBLISHTYPE.FILE_DIFF, Constants.MACADDRESSFROMENV+"_"+containerId,fileDiff);
				
				boolean present= fileDiff.stream().filter(k->k.getPath().equals(Constants.ROOT_BASH_HISTORY)).findFirst().isPresent();
				
				if(present) {List<String> commands = collect.command(containerId);
				publisher.publish(PUBLISHTYPE.COMMANDS, Constants.MACADDRESSFROMENV+"_"+containerId,commands);
				}
			}
		};
		
		try {
			dockerClientUtil.getClient().eventsCmd().exec(callback).awaitCompletion();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
