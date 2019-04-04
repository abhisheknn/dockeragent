package com.micro.policy;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.EventType;
import com.github.dockerjava.core.command.EventsResultCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.micro.client.docker.Collect;
import com.micro.client.docker.DockerAction;
import com.micro.client.docker.DockerClientUtil;
import com.micro.client.publish.common.Constants;

@Component
public class PolicyEnforcer {
	Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	@Autowired
	Collect collect;

	@Autowired
	private DockerClientUtil dockerClientUtil;

	@Autowired
	private DockerAction dockerAction;

	@Autowired
	WhiteList whiteList;

	@Autowired
	Blacklist blackList;

	@PostConstruct
	public void setup() {
		EventsResultCallback callback = new EventsResultCallback() {
			@Override
			public void onNext(Event event) {
				if (event.getType() == EventType.CONTAINER)
					if (event.getAction().equals(Constants.STARTACTION)) {
						takeActionOnContainer(event);
					}

				if (event.getType() == EventType.NETWORK)
					// collectNetwork(event);
					if (event.getType() == EventType.VOLUME)
						// collectVolume(event);
						super.onNext(event);

			}
		};
	}

	private void takeActionOnContainer(Event event) {

		String containerId = event.getId();
		Map<String, String> attributes = event.getActor().getAttributes();
		String image = attributes.get("image");
		boolean isImageBlackListed = blackList.isImagePresent(image); // black listed as part of policy
		dockerAction.killContainer(containerId);
	}

	public void applyPolicy(String payload) {
		Policy policy = gson.fromJson(payload, Policy.class);
		applyRules(policy.getRules());
		storePolicy(policy);
	}

	private void storePolicy(Policy policy) {
		// TODO Auto-generated method stub

	}

	private void applyRules(List<Map<String, String>> rules) {
		for (Map<String, String> rule : rules) {
			String ruleExpression = rule.get("ruleExpression");
			String ruleType = rule.get("ruleType");
			if (ruleType.equals(RuleType.CONTAINER)) {
				evaluateContainerExpression(ruleExpression);
			}
		}
	}

	private void evaluateContainerExpression(String ruleExpression) {
		String[] rules = ruleExpression.split(",");
		for (String rule : rules) {
			if (rule.contains("image")) {
				if (rule.startsWith("!")) {
					String[] imageRule = rule.split("=");
					String imageName = imageRule[imageRule.length - 1];
					blackList.addBlackListImage(imageName);
					killContainerWithImage(imageName);
				}
			} else if (rule.contains("volume")) {

			} else if (rule.contains("volume")) {

			}

		}
	}

	private void killContainerWithImage(String imageName) {

		List<Container> containers = collect.containers();
		containers.stream().filter((container) -> {
			return container.getImage().equals(imageName);
		}).forEach((container) -> {
			dockerAction.killContainer(container.getId());
		});
	}

}
