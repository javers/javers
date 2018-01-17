package org.javers.core.examples;

public class TenderAirline {

	// code and tenderLocation create the unique key
	private String code;
	private TenderLocation tenderLocation;

	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public TenderLocation getTenderLocation() {
		return tenderLocation;
	}

	public void setTenderLocation(TenderLocation tenderLocation) {
		this.tenderLocation = tenderLocation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

		TenderAirline other = (TenderAirline) obj;
		if (this.code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!this.code.equals(other.code)) {
			return false;
		}

		if (this.tenderLocation == null) {
			if (other.tenderLocation != null) {
				return false;
			}
		} else if (!this.tenderLocation.equals(other.tenderLocation)) {
			return false;
		}

		return true;
	}
}
