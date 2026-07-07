package org.javers.core.cases;

import java.util.List;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Adrien-dev25
 */
public class Case1494CompareComplexeListAsSetTest {

    // Configure javers with ListCompareAlgorithm.AS_SET
    private final static Javers JAVERS = JaversBuilder
            .javers()
            .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
            .build();

    static class Country {
        public List<City> cities;

        public Country(List<City> cities) {
            this.cities = cities;
        }
    }

    static class City {
        public List<String> streets;

        public City(List<String> streets) {
            this.streets = streets;
        }
    }


    @Test
    public void compareObjectsContainingListOfObjectsContainingListOfStrings() {
        // GIVEN Two objects containing List of objects containing List of String
        Country country1 = new Country(List.of(new City(List.of("Street A"))));
        Country country2 = new Country(List.of(new City(List.of("Street C"))));

        // WHEN compare with javers
        Diff diff = JAVERS.compare(country1, country2);

        // THEN we expect no java.lang.ClassCastException to be thrown
        Assertions.assertTrue(diff.hasChanges());
    }

}
