package org.javers.core.cases;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * <a href="https://github.com/javers/javers/issues/1485">Javers issues 1485</a>
 *
 * @author Adrien-dev25
 */
public class Case1484CompareComplexObjectGraphWithSetsTest {

    private final static Javers JAVERS = JaversBuilder.javers().build();

    static class Country {
        public Set<City> cities;

        public Country(Set<City> cities) {
            this.cities = cities;
        }
    }

    static class City {
        public Set<String> streets;

        public City(Set<String> streets) {
            this.streets = streets;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof City))
                return false;

            return Objects.equals(streets, ((City)obj).streets);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(streets);
        }
    }

    @Test
    public void compareSetsOfElementsInDifferentOrder() {
        // GIVEN Two Sets with elements in different order
        Set<String> streets1 = Set.of("Street A", "Street B");
        Set<String> streets2 = Set.of("Street B", "Street A");

        // WHEN compare with javers
        Diff diff = JAVERS.compare(streets1, streets2);

        // THEN we expect no change to be detected
        Assertions.assertFalse(diff.hasChanges());
    }

    @Test
    public void compareObjectsContainingSetOfElementsInDifferentOrder() {
        // GIVEN Two objects containing Set with elements in different order
        City city1 = new City(Set.of("Street A", "Street B"));
        City city2 = new City(Set.of("Street B", "Street A"));

        // WHEN compare with javers
        Diff diff = JAVERS.compare(city1, city2);

        // THEN we expect no change to be detected
        Assertions.assertFalse(diff.hasChanges());
    }

    @Test
    public void compareSetsOfObjectsContainingSetOfElementsInDifferentOrder() {
        // GIVEN Two sets of objects containing Set with elements in different order
        Set<City> cities1 = Set.of(new City(Set.of("Street A", "Street B")));
        Set<City> cities2 = Set.of(new City(Set.of("Street B", "Street A")));

        // WHEN compare with javers
        Diff diff = JAVERS.compare(cities1, cities2);

        // THEN we expect no change to be detected
        // Be careful : Methods City.equals() and City.hashCode() MUST be override
        // because internally JAVERS use a HashMap to detect changes between two Sets
        // Without equals() and hashCode() changes are ALWAYS detected (even if order of streets is the same)
        Assertions.assertFalse(diff.hasChanges());
    }

    @Test
    public void compareObjectsContainingSetOfObjectsContainingSetOfElementsInDifferentOrder() {
        // GIVEN Two objects containing set of objects containing Set with elements in different order
        Country country1 = new Country(Set.of(new City(Set.of("Street A", "Street B"))));
        Country country2 = new Country(Set.of(new City(Set.of("Street B", "Street A"))));

        // WHEN compare with javers
        Diff diff = JAVERS.compare(country1, country2);

        // THEN we expect no change to be detected
        Assertions.assertFalse(diff.hasChanges());
    }

    @Test
    public void compareObjectsWithLinkedHashSetInReverseInsertionOrder() {
        // Force a specific iteration order via LinkedHashSet
        Set<String> streets1 = new LinkedHashSet<>(List.of("Street A", "Street B"));
        Set<String> streets2 = new LinkedHashSet<>(List.of("Street B", "Street A"));

        Country country1 = new Country(Set.of(new City(streets1)));
        Country country2 = new Country(Set.of(new City(streets2)));

        // WHEN compare with javers
        Diff diff = JAVERS.compare(country1, country2);

        // THEN we expect no change to be detected
        Assertions.assertFalse(diff.hasChanges());
    }
}
