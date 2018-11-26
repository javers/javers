package org.javers.core.examples

import org.javers.common.collections.Sets
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.container.ContainerElementChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.changetype.container.ValueRemoved
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.metamodel.property.Property
import spock.lang.Specification

class CustomPropertyComparatorExample extends Specification {

    class Entity {
        String value
        List<String> values
    }

    /**
     * Compares Strings as character sets
     */
    class FunnyStringComparator implements CustomPropertyComparator<String, SetChange> {
        @Override
        Optional<SetChange> compare(String left, String right, GlobalId affectedId, Property property) {
            if (equals(left, right)) {
                return Optional.empty()
            }

            Set leftSet = left.toCharArray().toSet()
            Set rightSet = right.toCharArray().toSet()

            List<ContainerElementChange> changes = []
            Sets.difference(leftSet, rightSet).forEach{c -> changes.add(new ValueRemoved(c))}
            Sets.difference(rightSet, leftSet).forEach{c -> changes.add(new ValueAdded(c))}

            return Optional.of(new SetChange(affectedId, property.getName(), changes))
        }

        @Override
        boolean equals(String a, String b) {
            a.toCharArray().toSet() == b.toCharArray().toSet()
        }
    }

    def "should use FunnyStringComparator to compare String properties"(){
        given:
        def javers = JaversBuilder.javers()
                .registerCustomComparator(new FunnyStringComparator(), String).build()

        when:
        def diff = javers.compare(new Entity(value: "aaa"), new Entity(value: "a"))
        println "first diff: "+ diff

        then:
        diff.changes.size() == 0

        when:
        diff = javers.compare(new Entity(value: "aaa"), new Entity(value: "b"))
        println "second diff: "+ diff

        then:
        diff.changes.size() == 1
        diff.changes[0] instanceof SetChange
        diff.changes[0].changes.size() == 2 // two item changes in this SetChange
    }

    def "should use FunnyStringComparator to compare Strings in lists"(){
        given:
        def javers = JaversBuilder.javers()
                .registerCustomComparator(new FunnyStringComparator(), String).build()

        when:
        def diff = javers.compare(new Entity(values: ["aaa"]), new Entity(values: ["a"]))
        println "first diff: "+ diff

        then:
        diff.changes.size() == 0

        when:
        diff = javers.compare(new Entity(values: ["aaa"]), new Entity(values: ["a", "bb"]))
        println "second diff: "+ diff

        then:
        diff.changes.size() == 1
        diff.changes[0] instanceof ListChange
        diff.changes[0].changes.size() == 1 // one item change in this ListChange
    }
}

