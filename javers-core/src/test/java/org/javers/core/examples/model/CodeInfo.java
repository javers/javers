package org.javers.core.examples.model;
import java.io.Serializable;

public class CodeInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

	String prop;

	public CodeInfo(String prop) {
		this.prop=prop;
	}
}
