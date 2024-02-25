package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.PropertyChangeType
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.graph.HashCodeObjectHasher

import static org.fest.assertions.api.Assertions.assertThat

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
            Javers plainJavers = JaversBuilder.javers()
                    .build()


        when:
            def oldSet = Set.of(new Person("tommy", "Tommy Smart"), new Person("tommy2", "Tommy Smart2"));
            def newSet = Set.of(new Person("tommy2", "Tommy Smart2"), new Person("tommy", "Tommy C. Smart"));
    
            def diff = javersWithHashCode.compareCollections(oldSet, newSet, Case1301ObjectHasher.Person.class)
            def plainDiff = plainJavers.compareCollections(oldSet, newSet, Case1301ObjectHasher.Person.class)

        then:
            def change = diff.getChangesByType(ValueChange.class).get(0);
            diff.changes.size() == 1
            
            change.getChangeType() == PropertyChangeType.PROPERTY_VALUE_CHANGED
            change.getPropertyName() == "name"
            change.getLeft() == "Tommy Smart"
            change.getRight() == "Tommy C. Smart"
            
            def plainChange = diff.getChangesByType(ValueChange.class).get(0);
            plainDiff.changes.size() == 5

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
