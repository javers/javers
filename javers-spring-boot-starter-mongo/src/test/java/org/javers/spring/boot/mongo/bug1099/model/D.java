package org.javers.spring.boot.mongo.bug1099.model;

public class D {

	private String city;
	private String country;

	public D(String city, String country) {
		this.city = city;
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}
	
}
