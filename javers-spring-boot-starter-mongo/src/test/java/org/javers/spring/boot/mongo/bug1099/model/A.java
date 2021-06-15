package org.javers.spring.boot.mongo.bug1099.model;

import java.util.List;
import java.util.Map;

public class A {

	private B b;
	private C c;
	private Map<String, List<E>> map;

	public A(B b, C c, Map<String, List<E>> map) {
		this.b = b;
		this.c = c;
		this.map = map;
	}

	public B getB() {
		return b;
	}

	public C getC() {
		return c;
	}

	public Map<String, List<E>> getMap() {
		return map;
	}

}
