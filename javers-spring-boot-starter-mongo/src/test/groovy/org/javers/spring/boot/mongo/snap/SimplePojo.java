package org.javers.spring.boot.mongo.snap;

public class SimplePojo implements ISimplePojo {
	public SimpleEnum name;

	public SimpleEnum getName() {
		return name;
	}

	public void setName(SimpleEnum name) {
		this.name = name;
	}
}
