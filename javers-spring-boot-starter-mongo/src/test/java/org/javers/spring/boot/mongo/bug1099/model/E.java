package org.javers.spring.boot.mongo.bug1099.model;

import java.util.List;

public class E {
		
	private String profile;
	private List<String> roles;
	
	public E(String profile, List<String> roles) {
		super();
		this.profile = profile;
		this.roles = roles;
	}

	public String getProfile() {
		return profile;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	
}
