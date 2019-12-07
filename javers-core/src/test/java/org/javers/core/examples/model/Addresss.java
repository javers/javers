package org.javers.core.examples.model;

public class Addresss implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
		String state;
		String city;
		CodeInfo codeInfo;
	
		public CodeInfo getCodeInfo() {
			return codeInfo;
		}
		public void setCodeInfo(CodeInfo codeInfo) {
			this.codeInfo = codeInfo;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public Addresss(String state, String city,CodeInfo codeInfo) {
			this.state = state;
			this.city = city;
			this.codeInfo = codeInfo;
		}
}
