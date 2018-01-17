package org.javers.core.examples;

import java.util.ArrayList;
import java.util.List;

public class DummyComplexEntityFactory {

	public static ComplexEntity getDummyComplexEntity() {
		ComplexEntity complexEntity = new ComplexEntity();
		complexEntity.setCode("entity");
		complexEntity.setName("name");
		complexEntity.addToTenderLocations(getDummyTenderLocations(1));
		return complexEntity;
	}

	private static List<TenderLocation> getDummyTenderLocations(int locations) {
		List<TenderLocation> tenderLocationsList = new ArrayList<>(locations);
		for (int j = 0; j < locations; ++j) {
			TenderLocation location = new TenderLocation();
			location.setLocation("BV" + j);
			location.setSize(10);
			location.addToTenderAirlines(getDummyTenderAirlines(1));
			tenderLocationsList.add(location);
		}
		return tenderLocationsList;
	}

	private static List<TenderAirline> getDummyTenderAirlines(int airlines) {
		List<TenderAirline> tenderAirlinesList = new ArrayList<>(airlines);
		for (int i = 0; i < airlines; ++i) {
			TenderAirline airline = new TenderAirline();
			airline.setCode("CODE" + i);
			tenderAirlinesList.add(airline);
		}
		return tenderAirlinesList;
	}
}
