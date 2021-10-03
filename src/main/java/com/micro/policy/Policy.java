package com.micro.policy;

import java.util.List;
import java.util.Map;

public class Policy {

	private String policyName ;
	private List<Map<String,String>> rules;
	public String getPolicyName() {
		return policyName;
	}
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	public List<Map<String, String>> getRules() {
		return rules;
	}
	public void setRules(List<Map<String, String>> rules) {
		this.rules = rules;
	}
}
