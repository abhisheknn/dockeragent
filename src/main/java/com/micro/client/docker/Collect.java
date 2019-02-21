package com.micro.client.docker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.command.TopContainerCmd;
import com.github.dockerjava.api.command.TopContainerResponse;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.model.ChangeLog;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.micro.client.publish.common.Constants;

@Component
public class Collect{

	@Autowired
	DockerClientUtil dockerClientUtil;
	
	public List<ChangeLog> fileDiff(String containerId) {
		return  dockerClientUtil.getClient().containerDiffCmd(containerId).exec();
		}
	
	public List<String> command(String containerId) {
		List<String> commands= new ArrayList<>();
		try(InputStream is= dockerClientUtil.getClient().copyArchiveFromContainerCmd(containerId, Constants.ROOT_BASH_HISTORY).exec();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is))){
				String command = null;
	            while ((command = reader.readLine()) != null) {
	            	commands.add(command);
	            }
			}catch(Exception e) {
			System.out.println(e);
		}
		return commands;
	}

	public String[][] process(String containerId) {
	
		TopContainerResponse containerResponse= dockerClientUtil.getClient().topContainerCmd(containerId).exec();
		return containerResponse.getProcesses();
	}

	public List<Network> network() {
		return dockerClientUtil.getClient().listNetworksCmd().exec();
	}

	public List<Container> containers() {
		return dockerClientUtil.getClient().listContainersCmd().exec();
	}

}
