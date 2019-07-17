package org.javers.spring.boot.mongo.snap;

public interface IExtendedPojo extends ISimplePojo {
	SimpleEnum getAnotherName();

	void setAnotherName(SimpleEnum anotherName);
}
