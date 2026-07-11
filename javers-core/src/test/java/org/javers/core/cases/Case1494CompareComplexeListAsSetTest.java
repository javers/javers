package org.javers.core.cases;

import java.util.List;
import java.util.Objects;

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

        @Override
        public boolean equals(Object obj) {
            return Objects.equals(streets, ((City)obj).streets);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(streets);
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

    @Test
    public void compareObjectsContainingListOfObjectsContainingListOfElementsInDifferentOrder() {
        // GIVEN Two objects containing List (AS_SET) of objects containing List (AS_SET) with elements in different order
        Country country1 = new Country(List.of(new City(List.of("Street A", "Street B"))));
        Country country2 = new Country(List.of(new City(List.of("Street B", "Street A"))));

        // WHEN compare with javers
        Diff diff = JAVERS.compare(country1, country2);

        // THEN we expect no change to be detected
        Assertions.assertFalse(diff.hasChanges());
    }

}
