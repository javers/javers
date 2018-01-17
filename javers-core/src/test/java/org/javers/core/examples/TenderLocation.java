package org.javers.core.examples;

import java.util.ArrayList;
import java.util.List;

public class TenderLocation {

	// location and complexEntity create the unique key
	private String location;
	private ComplexEntity complexEntity;

	private int size;

	public List<TenderAirline> tenderAirlines;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ComplexEntity getComplexEntity() {
		return complexEntity;
	}

	public void setComplexEntity(ComplexEntity complexEntity) {
		this.complexEntity = complexEntity;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<TenderAirline> getTenderAirlines() {
		return tenderAirlines;
	}

	public void setTenderAirlines(List<TenderAirline> tenderAirlines) {
		this.tenderAirlines = tenderAirlines;
	}

	public void addToTenderAirlines(List<TenderAirline> airlines) {
		if (this.tenderAirlines == null) {
			this.tenderAirlines = new ArrayList<>();
		}
		for (TenderAirline airline : airlines) {
			airline.setTenderLocation(this);
			this.tenderAirlines.add(airline);
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

		TenderLocation other = (TenderLocation) obj;
		if (this.location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!this.location.equals(other.location)) {
			return false;
		}

		if (this.complexEntity == null) {
			if (other.complexEntity != null) {
				return false;
			}
		} else if (!this.complexEntity.equals(other.complexEntity)) {
			return false;
		}

		return true;
	}
}
