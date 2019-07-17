package org.javers.spring.boot.mongo.snap;

public class ExtendedPojo extends SimplePojo implements IExtendedPojo {
	private SimpleEnum anotherName;

	public SimpleEnum getAnotherName() {
		return anotherName;
	}

	public void setAnotherName(SimpleEnum anotherName) {
		this.anotherName = anotherName;
	}
}
