package org.javers.core

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.diff.changetype.PropertyChange
import org.javers.core.diff.changetype.PropertyChangeMetadata
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.property.Property
import org.javers.core.model.DummyAddress
import org.javers.core.model.GuavaObject
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.model.DummyAddress.Kind.HOME
import static org.javers.core.model.DummyAddress.Kind.OFFICE

/**
 * @author bartosz.walacik
 */
class CustomPropertyComparatorE2ETest extends Specification {

    private class CaseIgnoringStringComparator implements CustomPropertyComparator {
        Optional<PropertyChange> compare(Object left, Object right, PropertyChangeMetadata metadata, Property property) {
            Optional.empty()
        }

        boolean equals(Object a, Object b) {
            return a.toLowerCase().equals(b.toLowerCase())
        }
    }

    @Unroll
    def "should support CustomType comparator for Lists with listCompareAlgorithm #listCompareAlgorithm" () {
        given:
        def left =  new DummyAddress(city:"NY", moreCities: ["LA"])
        def right = new DummyAddress(city:"NY", moreCities: ["la", "Paris"])

        def javers = JaversBuilder.javers()
                .registerCustomComparator( new CaseIgnoringStringComparator(), String)
                .withListCompareAlgorithm(listCompareAlgorithm)
                .build()

        when:
        def diff = javers.compare(left,right)

        then:

        diff.changes.size() == 1
        ListChange change = diff.changes[0]
        change.changes.size() == 1
        with(change.changes[0]) {
            it instanceof ValueAdded
            it.addedValue == "Paris"
        }

        where:
        listCompareAlgorithm << [ListCompareAlgorithm.SIMPLE, ListCompareAlgorithm.AS_SET]
    }

    @Unroll
    def "should properly calculate diff using CustomType comparator and ListCompareAlgorithm.AS_SET where left: #leftList, right: #rightList" () {
        given:
        def left =  new DummyAddress(city:"NY", moreCities: leftList)
        def right = new DummyAddress(city:"NY", moreCities: rightList)

        def javers = JaversBuilder.javers()
                .registerCustomComparator( new CaseIgnoringStringComparator(), String)
                .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
                .build()

        when:
        def diff = javers.compare(left,right)

        then:
        if (expectedChanges == 0) {
            assert diff.changes.size() == 0
        }
        else {
            diff.changes.size() == 1
            ListChange change = diff.changes[0]
            assert change.changes.size() == expectedChanges
        }

        where:
        leftList                  | rightList                    | expectedChanges
        ["la", "LA", "lA"]        | ["Paris", "La", "paris"]     | 2  // added:   'Paris', 'paris'
        ["Paris", "La"]           | ["la", "LA", "lA"]           | 1  // removed: 'Paris'
        null                      | ["la"]                       | 1
        ["la"]                    | []                           | 1
        []                        | []                           | 0
        ["la"]                    | ["LA"]                       | 0
    }

    def "should support more than one custom comparator"(){
        given:
        def left =  new DummyAddress(city:"NY", kind:HOME)
        def right = new DummyAddress(city:"Paris", kind:OFFICE)

        when:
        def javers = JaversBuilder.javers().build()

        then:
        javers.compare(left,right).changes.size() == 2

        when:
        javers = JaversBuilder.javers()
                .registerCustomComparator( new DummyCustomPropertyComparator(), String)
                .registerCustomComparator( new DummyCustomPropertyComparator(), DummyAddress.Kind)
                .build()

        then:
        javers.compare(left,right).changes.size() == 0
    }

    private class DummyCustomPropertyComparator implements CustomPropertyComparator {
        Optional<PropertyChange> compare(Object left, Object right, PropertyChangeMetadata metadata, Property property) {
            Optional.empty()
        }

        boolean equals(Object a, Object b) {
            return false
        }
    }

    private class CustomMultimapFakeComparator implements CustomPropertyComparator<Multimap, MapChange>{
        Optional<MapChange> compare(Multimap left, Multimap right, PropertyChangeMetadata metadata, Property property) {
            return Optional.of(new MapChange(metadata, [new EntryValueChange("a", left.get("a")[0], right.get("a")[0])]))
        }

        boolean equals(Multimap a, Multimap b) {
            return false
        }
    }

    def "should support custom comparator for custom types"() {
        given:
        def left =  new GuavaObject(multimap: Multimaps.forMap(["a":1]))
        def right = new GuavaObject(multimap: Multimaps.forMap(["a":2]))
        def javers = JaversBuilder.javers()
                .registerCustomComparator(new CustomMultimapFakeComparator(), Multimap).build()

        when:
        def diff = javers.compare(left,right)

        then:
        diff.changes.size() == 1
        with(diff.changes[0]) {
            affectedGlobalId instanceof UnboundedValueObjectId
            propertyName == "multimap"
            changes[0].key == "a"
            changes[0].leftValue == 1
            changes[0].rightValue == 2
        }
    }
}
