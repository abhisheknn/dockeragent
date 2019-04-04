package com.micro.policy;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class Blacklist {

	private Set<String> images;
	
	public Blacklist() {
		// get all policies for this machine using rest api if machinewend down and came up .
		images= new HashSet<>();
	}
	
	public boolean isImagePresent(String image) {
		return images.contains(image);
	}

	public void addBlackListImage(String image) {
		images.add(image);
	}
	
	public void removeBlackListImage(String image) {
		images.remove(image);
	}
	
}
