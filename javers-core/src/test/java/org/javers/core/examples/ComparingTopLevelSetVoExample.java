package org.javers.core.examples;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.PropertyChangeType;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.examples.vo.Person;
import org.javers.core.graph.HashCodeObjectHasher;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author tomas.kupka
 */
public class ComparingTopLevelSetVoExample {

    private Set<Person> oldSet = Set.of(new Person("tommy", "Tommy Smart"), new Person("tommy2", "Tommy Smart2"));
    private Set<Person> newSet = Set.of(new Person("tommy2", "Tommy Smart2"), new Person("tommy", "Tommy C. Smart"));

    @Test
    public void shouldDeeplyCompareTwoTopLevelCollectionsWithDefaultObjectHasher() {
        //given
        Javers javers = JaversBuilder.javers()
            .build();

        //when
        Diff diff = javers.compareCollections(oldSet, newSet, Person.class);

        System.out.println(diff.prettyPrint());

        assertThat(diff.getChanges()).hasSize(5);

        //then
        //there should be four change of type {@link ValueChange}
        ValueChange change = diff.getChangesByType(ValueChange.class).get(0);
        List objectsWithChangedProperty = diff.getObjectsWithChangedProperty("login");
        Person first = (Person) objectsWithChangedProperty.get(0);
        Person second = (Person) objectsWithChangedProperty.get(1);
        //check initial object data
        assertThat(first.getLogin().equals(second.getLogin()));
        assertThat(first.equals(second));
        assertThat(first.hashCode() == second.hashCode());
        assertThat(!first.getName().equals(second.getName()));
        
        objectsWithChangedProperty = diff.getObjectsWithChangedProperty("name");
        first = (Person) objectsWithChangedProperty.get(0);
        second = (Person) objectsWithChangedProperty.get(1);
        assertThat(first.getLogin().equals(second.getLogin()));
        assertThat(first.equals(second));
        assertThat(first.hashCode() == second.hashCode());
        assertThat(!first.getName().equals(second.getName()));
        
        change = findValueChange(diff, "login", true);
        assertThat(change.getChangeType()).isEqualTo(PropertyChangeType.PROPERTY_VALUE_CHANGED);
        assertThat(change.getPropertyName()).isEqualTo("login");
        assertThat(change.getLeft()).isNull();
        assertThat(change.getRight()).isEqualTo("tommy");

        change = findValueChange(diff, "name", true);
        assertThat(change.getChangeType()).isEqualTo(PropertyChangeType.PROPERTY_VALUE_CHANGED);
        assertThat(change.getPropertyName()).isEqualTo("name");
        assertThat(change.getLeft()).isNull();
        assertThat(change.getRight()).isEqualTo("Tommy C. Smart");

        change = findValueChange(diff, "login", false);
        assertThat(change.getChangeType()).isEqualTo(PropertyChangeType.PROPERTY_VALUE_CHANGED);
        assertThat(change.getPropertyName()).isEqualTo("login");
        assertThat(change.getRight()).isNull();
        assertThat(change.getLeft()).isEqualTo("tommy");

        change = findValueChange(diff, "name", false);
        assertThat(change.getChangeType()).isEqualTo(PropertyChangeType.PROPERTY_VALUE_CHANGED);
        assertThat(change.getPropertyName()).isEqualTo("name");
        assertThat(change.getRight()).isNull();
        assertThat(change.getLeft()).isEqualTo("Tommy Smart");

        SetChange changeSet = diff.getChangesByType(SetChange.class).get(0);
        assertThat(changeSet.getChangeType()).isEqualTo(PropertyChangeType.PROPERTY_VALUE_CHANGED);
        assertThat(changeSet.getPropertyName()).isEqualTo("set");
        assertThat(changeSet.getLeft().size()).isEqualTo(2);
        assertThat(changeSet.getRight().size()).isEqualTo(2);

    }
    
    private ValueChange findValueChange(Diff diff, String propertyName, boolean leftNull) {
        List<Change> changes = diff.getChanges(change -> {
        	if (change instanceof ValueChange) {
        		ValueChange valueChange = (ValueChange) change;
        		return valueChange.getPropertyName().equals(propertyName) && Objects.isNull(valueChange.getLeft()) == leftNull;
        	}
        	return false;
        });
        return changes.stream()
        	.map(ValueChange.class::cast)
        	.findFirst()
        	.orElse(null);
    }

    @Test
    public void shouldDeeplyCompareTwoTopLevelCollectionsWithHashCodeObjectHasher() {
        //given
        Javers javers = JaversBuilder.javers()
            .registerObjectHasher(HashCodeObjectHasher.class)
            .build();

        //when
        Diff diff = javers.compareCollections(oldSet, newSet, Person.class);

        System.out.println(diff.prettyPrint());

        assertThat(diff.getChanges()).hasSize(1);

        //then
        //there should be one change of type {@link ValueChange}
        ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

        assertThat(change.getChangeType()).isEqualTo(PropertyChangeType.PROPERTY_VALUE_CHANGED);
        assertThat(change.getPropertyName()).isEqualTo("name");
        assertThat(change.getLeft()).isEqualTo("Tommy Smart");
        assertThat(change.getRight()).isEqualTo("Tommy C. Smart");

    }

}
