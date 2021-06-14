package org.javers.spring.boot.mongo.bug1099.model;

public class C {
	private String companyName;
	private D address;
	
	public C(String companyName, D address) {
		this.companyName = companyName;
		this.address = address;
	}

	public String getCompanyName() {
		return companyName;
	}
	
	public D getAddress() {
		return address;
	}
	
	
}
