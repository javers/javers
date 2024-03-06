package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Change
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.PropertyChangeType
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.graph.HashCodeObjectHasher

import groovy.transform.EqualsAndHashCode
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/1301
 */
class Case1301ObjectHasher extends Specification {

    def "should produce single change when object is identified by hashCode"(){
        given:
            Javers javersWithHashCode = JaversBuilder.javers()
                    .registerObjectHasher(HashCodeObjectHasher.class)
                    .build()
            def oldSet = Set.of(new Person("tommy", "Tommy Smart"), new Person("tommy2", "Tommy Smart2"));
            def newSet = Set.of(new Person("tommy2", "Tommy Smart2"), new Person("tommy", "Tommy C. Smart"));

        when:

            def diff = javersWithHashCode.compareCollections(oldSet, newSet, Case1301ObjectHasher.Person.class)

        then:
            def change = diff.getChangesByType(ValueChange.class).get(0);
            diff.changes.size() == 1
            
            change.getChangeType() == PropertyChangeType.PROPERTY_VALUE_CHANGED
            change.getPropertyName() == "name"
            change.getLeft() == "Tommy Smart"
            change.getRight() == "Tommy C. Smart"
    }

   def "should produce multiple changes when object is identified by JSON"(){
        given:
            Javers plainJavers = JaversBuilder.javers()
                    .build()
            def oldSet = Set.of(new Person("tommy", "Tommy Smart"), new Person("tommy2", "Tommy Smart2"));
            def newSet = Set.of(new Person("tommy2", "Tommy Smart2"), new Person("tommy", "Tommy C. Smart"));


        when:
    
            def diff = plainJavers.compareCollections(oldSet, newSet, Case1301ObjectHasher.Person.class)
            def change1 = findValueChange(diff, "login", true);
            def change2 = findValueChange(diff, "name", true);
            def change3 = findValueChange(diff, "login", false);
            def change4 = findValueChange(diff, "name", false);
            def changeSet = diff.getChangesByType(SetChange.class).get(0);

        then:

            diff.changes.size() == 5

            change1.getChangeType() == PropertyChangeType.PROPERTY_VALUE_CHANGED
            change1.getPropertyName() == "login"
            change1.getLeft() == null
            change1.getRight() == "tommy"
            
            change2.getChangeType() == PropertyChangeType.PROPERTY_VALUE_CHANGED
            change2.getPropertyName() == "name"
            change2.getLeft() == null
            change2.getRight() == "Tommy C. Smart"
            
            
            change3.getChangeType() == PropertyChangeType.PROPERTY_VALUE_CHANGED
            change3.getPropertyName() == "login"
            change3.getLeft() == "tommy"
            change3.getRight() == null
            
            
            change4.getChangeType() == PropertyChangeType.PROPERTY_VALUE_CHANGED
            change4.getPropertyName() == "name"
            change4.getLeft() == "Tommy Smart"
            change4.getRight() == null
            
            changeSet.getChangeType() == PropertyChangeType.PROPERTY_VALUE_CHANGED
            changeSet.getPropertyName() == "set"
            changeSet.getLeft().size() == 2
            changeSet.getRight().size() == 2
    }

	private def findValueChange(Diff diff, String propertyName, boolean leftNull) {
		List<Change> changes = diff.getChanges{change -> 
        	if (change instanceof ValueChange) {
                ValueChange valueChange = (ValueChange) change
                return valueChange.getPropertyName().equals(propertyName) && Objects.isNull(valueChange.getLeft()) == leftNull
            }
            return false
        };
        return changes.stream()
            .findFirst()
            .orElse(null)
	}

    @EqualsAndHashCode(includes = ["login"], includeFields = true)
    class Person {
        private String login
        private String name

        Person(String login, String name) {
            this.login = login
            this.name = name
        }
    }
}
