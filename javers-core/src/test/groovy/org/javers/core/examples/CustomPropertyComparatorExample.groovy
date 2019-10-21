package org.javers.core.examples

import org.javers.common.collections.Sets
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.PropertyChangeMetadata
import org.javers.core.diff.changetype.container.*
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.property.Property
import spock.lang.Specification

class CustomPropertyComparatorExample extends Specification {

    class ValueObject {
        String value
    }

    /**
     * Compares Strings as character sets
     */
    class FunnyStringComparator implements CustomPropertyComparator<String, SetChange> {
        @Override
        Optional<SetChange> compare(String left, String right, PropertyChangeMetadata metadata, Property property) {
            if (equals(left, right)) {
                return Optional.empty()
            }

            Set leftSet = left.toCharArray().toSet()
            Set rightSet = right.toCharArray().toSet()

            List<ContainerElementChange> changes = []
            Sets.difference(leftSet, rightSet).forEach{c -> changes.add(new ValueRemoved(c))}
            Sets.difference(rightSet, leftSet).forEach{c -> changes.add(new ValueAdded(c))}

            return Optional.of(new SetChange(metadata, changes))
        }

        @Override
        boolean equals(String a, String b) {
            a.toCharArray().toSet() == b.toCharArray().toSet()
        }

        @Override
        String toString(String value) {
            return value;
        }
    }

    def "should use FunnyStringComparator to compare String properties"(){
        given:
        def javers = JaversBuilder.javers()
                .registerCustomType(String, new FunnyStringComparator()).build()

        when:
        def diff = javers.compare(new ValueObject(value: "aaa"), new ValueObject(value: "a"))
        println "first diff: "+ diff

        then:
        diff.changes.size() == 0

        when:
        diff = javers.compare(new ValueObject(value: "aaa"), new ValueObject(value: "b"))
        println "second diff: "+ diff

        then:
        diff.changes.size() == 1
        diff.changes[0] instanceof SetChange
        diff.changes[0].changes.size() == 2 // two item changes in this SetChange
    }
}

