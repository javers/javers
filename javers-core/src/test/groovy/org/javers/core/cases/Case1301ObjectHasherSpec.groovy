package org.javers.core.cases


import org.javers.core.JaversBuilder
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
class Case1301ObjectHasherSpec extends Specification {

    def "should produce single change when Value Object is identified by hashCode"(){
        given:
        def javersWithHashCode = JaversBuilder.javers()
                .registerObjectHasher(HashCodeObjectHasher.class)
                .build()
        def oldSet = Set.of(new Person("tommy", "Tommy Smart"), new Person("tommy2", "Tommy Smart2"));
        def newSet = Set.of(new Person("tommy2", "Tommy Smart2"), new Person("tommy", "Tommy C. Smart"));

        when:
        def diff = javersWithHashCode.compareCollections(oldSet, newSet, Case1301ObjectHasherSpec.Person.class)
        println(diff)

        then:
        def change = diff.getChangesByType(ValueChange.class).get(0);
        diff.changes.size() == 1

        change.getChangeType() == PropertyChangeType.PROPERTY_VALUE_CHANGED
        change.getPropertyName() == "name"
        change.getLeft() == "Tommy Smart"
        change.getRight() == "Tommy C. Smart"
    }

   def "should produce multiple changes when Value Object is identified by JSON state"(){
        given:
        def plainJavers = JaversBuilder.javers().build()
        def oldSet = Set.of(new Person("tommy", "Tommy Smart"), new Person("tommy2", "Tommy Smart2"));
        def newSet = Set.of(new Person("tommy2", "Tommy Smart2"), new Person("tommy", "Tommy C. Smart"));

        when:
        def diff = plainJavers.compareCollections(oldSet, newSet, Case1301ObjectHasherSpec.Person.class)

        then:
        diff.changes.size() == 5

        def change1 =   findValueChange(diff, "login", true)
        change1.left == null
        change1.right == "tommy"

        def change2 = findValueChange(diff, "name", true)
        change2.left == null
        change2.right == "Tommy C. Smart"

        def change3 = findValueChange(diff, "login", false)
        change3.left == "tommy"
        change3.right == null

        def change4 = findValueChange(diff, "name", false)
        change4.left == "Tommy Smart"
        change4.right == null

        def setChange = diff.getChangesByType(SetChange.class)[0]
        setChange.propertyName == "set"
        setChange.left.size() == 2
        setChange.right.size() == 2
    }

	private ValueChange findValueChange(Diff diff, String propertyName, boolean leftNull) {
        return diff.getChangesByType(ValueChange).find(change -> {
            change.propertyName == propertyName && Objects.isNull(change.left) == leftNull
        })
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
