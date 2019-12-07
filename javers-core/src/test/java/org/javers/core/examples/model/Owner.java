package org.javers.core.examples.model;

public class Owner implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	int id;
	Address address;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
			
	Owner(int id, Address address){
		this.id=id;
		this.address=address;
	}
}
