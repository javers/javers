package org.javers.core

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import org.javers.core.diff.changetype.PropertyChangeMetadata
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.metamodel.property.Property
import org.javers.core.model.DummyAddress
import org.javers.core.model.GuavaObject
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.diff.ListCompareAlgorithm.AS_SET
import static org.javers.core.diff.ListCompareAlgorithm.SIMPLE
import static org.javers.core.model.DummyAddress.Kind.HOME
import static org.javers.core.model.DummyAddress.Kind.OFFICE

/**
 * @author bartosz.walacik
 */
class CustomPropertyComparatorE2ETest extends Specification {

    private class CaseIgnoringCustomComparator implements CustomPropertyComparator {
        Optional compare(Object left, Object right, PropertyChangeMetadata metadata, Property property) {
            return Optional.empty()
        }

        boolean equals(Object a, Object b) {
            return a.toLowerCase().equals(b.toLowerCase())
        }

        @Override
        String toString(Object value) {
            return value.toLowerCase()
        }
    }

    class AddressesList {
        List<DummyAddress> addresses

        AddressesList(List<DummyAddress> addresses) {
            this.addresses = addresses
        }
    }

    @Unroll
    def "should compare List of ValueObjects with #what using #listCompareAlgorithm algorithm" () {
        given:
        def left =  new AddressesList([new DummyAddress(city:"NY"), new DummyAddress(city:null)])
        def right = new AddressesList([new DummyAddress(city:"ny"), new DummyAddress(city:null),
                                       new DummyAddress(city:"ny", street: "some")])

        def javers = JaversBuilder.javers()
        registerComparator(javers)
        javers.withListCompareAlgorithm(listCompareAlgorithm)
        javers = javers.build()

        when:
        def diff = javers.compare(left, right)

        then:
        println(diff.prettyPrint())

        diff.getChangesByType(ValueChange).size() == 0
        diff.getChangesByType(ListChange).size() == 1
        with(diff.getChangesByType(ListChange)[0]) {
            changes.size() == 1
            changes[0] instanceof ValueAdded
            changes[0].addedValue instanceof ValueObjectId
            changes[0].addedValue.typeName.endsWith('DummyAddress')

        }

        where:
        listCompareAlgorithm << [SIMPLE, AS_SET] * 2
        what << ["CustomTypes"] * 2 + ["Values"] * 2
        registerComparator <<
                [{builder -> builder.registerCustomType(String, new CaseIgnoringCustomComparator())}] * 2 +
                [{builder -> builder.registerValue(String, new CaseIgnoringCustomComparator()) }] * 2
    }

    class AddressesSet {
        Set<DummyAddress> addresses

        AddressesSet(Set<DummyAddress> addresses) {
            this.addresses = addresses
        }
    }

    @Unroll
    def "should compare Set of ValueObjects with #what" () {
        given:
        def left =  new AddressesSet([new DummyAddress(city:"NY")] as Set)
        def right = new AddressesSet([new DummyAddress(city:"ny"),
                                      new DummyAddress(city:"ny", street: "some")] as Set)

        def javers = JaversBuilder.javers()
        registerComparator(javers)
        javers = javers.build()

        when:
        def diff = javers.compare(left, right)

        then:
        println(diff.prettyPrint())

        diff.getChangesByType(ValueChange).size() == 0
        diff.getChangesByType(SetChange).size() == 1
        with(diff.getChangesByType(SetChange)[0]) {
            changes.size() == 1
            changes[0] instanceof ValueAdded
            changes[0].addedValue instanceof ValueObjectId
            changes[0].addedValue.value().endsWith('$AddressesSet/#addresses/d7713b458f3a759a942fcd6fa8058979')
        }

        where:
        what << ["CustomTypes", "Values"]
        registerComparator << [
                {builder -> builder.registerCustomType(String, new CaseIgnoringCustomComparator())},
                {builder -> builder.registerValue(String, new CaseIgnoringCustomComparator())}
        ]
    }

    @Unroll
    def "should compare List of #what using #listCompareAlgorithm algorithm" () {
        given:
        def left =  new DummyAddress(city:"NY", moreCities: ["LA"])
        def right = new DummyAddress(city:"NY", moreCities: ["la", "Paris"])

        def javers = JaversBuilder.javers()
        registerComparator(javers)
        javers.withListCompareAlgorithm(listCompareAlgorithm)
        javers = javers.build()

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
        listCompareAlgorithm << [SIMPLE, AS_SET] * 2
        what << ["CustomTypes"] * 2 + ["Values"] * 2
        registerComparator <<
            [{builder -> builder.registerCustomType(String, new CaseIgnoringCustomComparator())}] * 2 +
            [{builder -> builder.registerValue(String, new CaseIgnoringCustomComparator()) }] * 2
    }

    class SetOfStrings {
        Set<String> strings
    }

    @Unroll
    def "should compare Set of #what using CustomComparator" () {
        given:
        def left =  new SetOfStrings(strings: ["LA"])
        def right = new SetOfStrings(strings: ["la", "Paris"])

        def javers = JaversBuilder.javers()
        registerComparator(javers)
        javers = javers.build()

        when:
        def diff = javers.compare(left,right)

        then:
        println(diff.prettyPrint())
        diff.changes.size() == 1
        SetChange change = diff.changes[0]
        change.changes.size() == 1
        with(change.changes[0]) {
            it instanceof ValueAdded
            it.addedValue == "Paris"
        }

        where:
        what << ["CustomTypes", "Values"]
        registerComparator << [
                {builder -> builder.registerCustomType(String, new CaseIgnoringCustomComparator())},
                {builder -> builder.registerValue(String, new CaseIgnoringCustomComparator()) }
        ]
    }

    class MapOfStrings {
        Map<String, String> strings
    }

    @Unroll
    def "should compare Map of #what using CustomComparator" () {
        given:
        def left =  new MapOfStrings(strings: ["LA":"paris", "AAA":"aa"])
        def right = new MapOfStrings(strings: ["la":"PARIS", "aAa":"bb"])

        def javers = JaversBuilder.javers()
        registerComparator(javers)
        javers = javers.build()

        when:
        def diff = javers.compare(left,right)

        then:
        println(diff.prettyPrint())
        diff.changes.size() == 1
        MapChange change = diff.changes[0]
        change.changes.size() == 1
        with(change.changes[0]) {
            it instanceof EntryValueChange
            it.key.toLowerCase() == "aaa"
            it.leftValue == "aa"
            it.rightValue == "bb"
        }

        where:
        what << ["CustomTypes", "Values"]
        registerComparator << [
                {builder -> builder.registerCustomType(String, new CaseIgnoringCustomComparator())},
                {builder -> builder.registerValue(String, new CaseIgnoringCustomComparator()) }
        ]
    }

    @Unroll
    def "should properly calculate diff of LIST_AS_SET of CustomTypes where left: #leftList, right: #rightList" () {
        given:
        def left =  new DummyAddress(city:"NY", moreCities: leftList)
        def right = new DummyAddress(city:"NY", moreCities: rightList)

        def javers = JaversBuilder.javers()
                .registerCustomType(String, new CaseIgnoringCustomComparator())
                .withListCompareAlgorithm(AS_SET)
                .build()

        when:
        def diff = javers.compare(left,right)

        then:
        println diff.prettyPrint()

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
        ["la", "LA", "lA"]        | ["Paris", "La", "paris"]     | 1  // added: 'paris'
        ["Paris", "La"]           | ["la", "LA", "lA"]           | 1  // removed: 'Paris'
        null                      | ["la"]                       | 1
        ["la"]                    | [null]                       | 2
        [null]                    | ["la"]                       | 2
        ["la"]                    | []                           | 1
        []                        | []                           | 0
        ["la"]                    | ["LA"]                       | 0
    }

    private class DummyCustomPropertyComparator implements CustomPropertyComparator {
        boolean equals(Object a, Object b) {
            return false
        }

        Optional compare(Object left, Object right, PropertyChangeMetadata metadata, Property property) {
            return Optional.empty()
        }

        String toString(Object value) {
            return value.toString()
        }
    }

    def "should support more than one CustomComparator"(){
        given:
        def left =  new DummyAddress(city:"NY", kind:HOME)
        def right = new DummyAddress(city:"Paris", kind:OFFICE)

        when:
        def javers = JaversBuilder.javers().build()

        then:
        javers.compare(left,right).changes.size() == 2

        when:
        javers = JaversBuilder.javers()
                .registerCustomType(String, new DummyCustomPropertyComparator() )
                .registerCustomType(DummyAddress.Kind, new DummyCustomPropertyComparator(), )
                .build()

        then:
        javers.compare(left,right).changes.size() == 0
    }

    private class CustomMultimapFakeComparator implements CustomPropertyComparator<Multimap, MapChange>{
        Optional<MapChange> compare(Multimap left, Multimap right, PropertyChangeMetadata metadata, Property property) {
            return Optional.of(new MapChange(metadata, [new EntryValueChange("a", left.get("a")[0], right.get("a")[0])]))
        }

        boolean equals(Multimap a, Multimap b) {
            return Object.equals(a, b)
        }

        String toString(Multimap value) {
            return value.toString()
        }
    }

    def "should support property-level CustomComparator for CustomTypes"() {
        given:
        def left =  new GuavaObject(multimap: Multimaps.forMap(["a":1]))
        def right = new GuavaObject(multimap: Multimaps.forMap(["a":2]))
        def javers = JaversBuilder.javers()
                .registerCustomType(Multimap, new CustomMultimapFakeComparator()).build()

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
