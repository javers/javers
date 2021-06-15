package org.javers.spring.boot.mongo.bug1099.model;

import org.javers.core.metamodel.annotation.Id;
//import org.springframework.data.annotation.Id;

public class B {
	
	@Id
	private String id;
	private String name;
	private int age;

	public B(String id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}
	
}
