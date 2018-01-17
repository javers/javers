package org.javers.core.examples;

import java.util.ArrayList;
import java.util.List;

public class ComplexEntity {

	// code is the unique key
	private String code;
	
	private List<TenderLocation> tenderLocationList;
	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<TenderLocation> getTenderLocationList() {
		return tenderLocationList;
	}

	public void setTenderLocationList(List<TenderLocation> tenderLocationList) {
		this.tenderLocationList = tenderLocationList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addToTenderLocations(List<TenderLocation> locations) {
		if (this.tenderLocationList == null) {
			this.tenderLocationList = new ArrayList<>();
		}
		for (TenderLocation location : locations) {
			location.setComplexEntity(this);
			this.tenderLocationList.add(location);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		ComplexEntity other = (ComplexEntity) obj;
		if (this.code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!this.code.equals(other.code)) {
			return false;
		}

		return true;
	}
}
