package org.javers.core.cases

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification

class CaseWithSortedCollectionPropertyType extends Specification {

    def javers = JaversBuilder
            .javers()
            .registerValueObject(DummyValueObject)
            .build()

    def "can detect new element added to set property"() {
        given:
        def a = new DummyEntity(
                1,
                "foo",
                [].toSet()
        )

        def b = new DummyEntity(
                a.id,
                a.name,
                a.valueObjectSet + new DummyValueObject("bar"),
        )

        when:
        def diff = javers.compare(a, b)
        println diff

        then:
        diff.hasChanges()
        diff.changes.any() { change ->
            change instanceof SetChange
        }
    }

    def "can detect new element added to sorted set property"() {
        given:
        def a = new DummyEntity(
                1,
                "foo",
                null,
                new TreeSet<DummyValueObject>()
        )

        def b = new DummyEntity(
                a.id,
                a.name,
                a.valueObjectSet,
                a.valueObjectSortedSet + new DummyValueObject("bar"),
        )

        when:
        def diff = javers.compare(a, b)
        println diff

        then:
        diff.hasChanges()
        diff.changes.any() { change ->
            change instanceof SetChange
        }
    }

    def "can detect new element added to map property"() {
        given:
        def a = new DummyEntity(
                1,
                "foo",
                null,
                null,
                [:]
        )

        def b = new DummyEntity(
                a.id,
                a.name,
                a.valueObjectSet,
                a.valueObjectSortedSet,
                a.valueObjectMap + ["3": new DummyValueObject("bar")]
        )

        when:
        def diff = javers.compare(a, b)
        println diff

        then:
        diff.hasChanges()
        diff.changes.any() { change ->
            change instanceof MapChange && !change.entryAddedChanges.isEmpty()
        }
    }

    def "can detect new element added to sorted map property"() {
        given:
        def a = new DummyEntity(
                1,
                "foo"
        )

        def sortedMap = new TreeMap<String, DummyValueObject>()
        sortedMap.put("3", new DummyValueObject("bar"))

        def b = new DummyEntity(
                a.id,
                a.name,
                a.valueObjectSet,
                a.valueObjectSortedSet,
                a.valueObjectMap,
                sortedMap
        )

        when:
        def diff = javers.compare(a, b)
        println diff

        then:
        diff.hasChanges()
        diff.changes.any() { change ->
            change instanceof MapChange && !change.entryAddedChanges.isEmpty()
        }
    }
}

@TupleConstructor
class DummyEntity {
    @Id
    final int id
    final String name
    final Set<DummyValueObject> valueObjectSet
    final SortedSet<DummyValueObject> valueObjectSortedSet
    final Map<String, DummyValueObject> valueObjectMap
    final SortedMap<String, DummyValueObject> valueObjectSortedMap
}

@ToString
@TupleConstructor
class DummyValueObject implements Comparable<DummyValueObject> {
    final String name

    @Override
    int compareTo(DummyValueObject that) {
        return name <=> that.name
    }
}
