package org.javers.guavasupport

import com.google.common.collect.HashMultimap
import com.google.common.collect.HashMultiset
import org.javers.common.exception.JaversException
import org.javers.core.Javers
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
import static org.javers.core.JaversBuilder.javers
import static org.javers.guavasupport.MultimapBuilder.create
/**
 * @author akrystian
 */
class GuavaAddOnE2ETest extends Specification {
    Javers javers

    void setup() {
        javers = javers().build()
    }

    @Unroll
    def "should detect changes in Multiset of primitives"() {
        given:
        def left = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 1
        diff.changes[0] instanceof SetChange
        diff.changes[0].changes.size() == expectedContainerChanges

        where:
        leftList     | rightList                          | expectedContainerChanges
        ["New York"] | ["Boston"]                         | 2
        ["New York"] | ["New York", "New York", "Boston"] | 2
        []           | ["New York"]                       | 1
        ["New York"] | []                                 | 1
    }



    @Unroll
    def "should not detect changes if Multisets of primitives are the same"() {
        given:
        def left = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 0

        where:
        leftList                 | rightList
        []                       | []
        ["New York"]             | ["New York"]
        ["New York", "New York"] | ["New York", "New York"]
    }

    @Unroll
    def "should detect changes in Multiset of ValueObjects"() {
        given:
        def left = new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        def actualContainerChanges = getContainerChanges(diff.changes)
        actualContainerChanges.size() == 1
        actualContainerChanges[0].changes.size() == expectedContainerChanges

        where:
        leftList                             | rightList                            | expectedContainerChanges
        [new DummyAddress(city: "New York")] | [new DummyAddress(city: "Buffalo")]  | 2
        [new DummyAddress(city: "New York")] | [new DummyAddress(city: "New York"),
                                                new DummyAddress(city: "New York")] | 1
        []                                   | [new DummyAddress(city: "New York")] | 1
        [new DummyAddress(city: "New York")] | []                                   | 1
    }

    def "should detect changes in Multiset of Entities"() {
        given:
        def left = new SnapshotEntity(multiSetOfEntities: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetOfEntities: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 1
        diff.changes[0] instanceof SetChange
        diff.changes[0].changes.size() == 1
        with(diff.changes[0].changes[0]){
            it instanceof ValueAdded
            it.addedValue.value() == SnapshotEntity.name + '/' + 2
        }

        where:
        leftList <<  [ [new SnapshotEntity(id:2), new SnapshotEntity(id:3)] ]
        rightList << [ [new SnapshotEntity(id:2), new SnapshotEntity(id:2), new SnapshotEntity(id:3)] ]
    }

    def "should follow Entities stored in Multisets when building ObjectGraph"(){
        given:
        def left = new SnapshotEntity(multiSetOfEntities: HashMultiset.create(
              [new SnapshotEntity(id:2)]
        ))
        def right = new SnapshotEntity(multiSetOfEntities: HashMultiset.create(
              [new SnapshotEntity(id:2, entityRef: new SnapshotEntity(id:3))]
        ))

        when:
        def diff = javers.compare(left, right)
        println diff.prettyPrint()

        then:
        diff.changes.size() == 2
        diff.getChangesByType(ReferenceChange).size == 1
        diff.getChangesByType(NewObject).size == 1
    }

    def "should not detect changes if Multisets of ValueObjects are the same"() {
        given:
        def left = new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 0

        where:
        leftList <<  [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York"),
                       new DummyAddress(city: "Buffalo")] ]
        rightList << [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York"),
                       new DummyAddress(city: "Buffalo")] ]
    }

    @Unroll
    def "should detect value changes in Multimap of primitives "() {
        given:
        def left = new SnapshotEntity(multiMapOfPrimitives: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multiMapOfPrimitives: HashMultimap.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 1
        diff.changes[0].entryChanges.size() == extpectedChanges

        where:
        leftList << [create( "New York": ["City"] ),
                     create( "New York": ["City"] ),
                     create( [:])]
        rightList <<[create( "New York": ["Buffalo"] ),
                     create( "New York": ["City", "Buffalo", "London"] ),
                     create( "New York": ["City"]) ]

        extpectedChanges << [2, 2, 1]
    }

    @Unroll
    def "should detect value changes in Multimap of ValueObjects "() {
        given:
        def left = new SnapshotEntity(multimapPrimitiveToValueObject: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multimapPrimitiveToValueObject: HashMultimap.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        def actualContainerChanges = getContainerChanges(diff.changes)
        actualContainerChanges.size() == 1
        actualContainerChanges[0].entryChanges.size() == expectedContainerChanges

        where:
        leftList << [
                create(["NY": [new DummyAddress("City")]]),
                create(["NY": [new DummyAddress("City")]]),
                HashMultimap.create(),
                create(["NY": [new DummyAddress("City"),
                                       new DummyAddress("Buffalo"),
                                       new DummyAddress("London")]]),
        ]
        rightList << [create(["NY": [new DummyAddress("Buffalo")]]),
                      create(["NY": [new DummyAddress("City"),
                                             new DummyAddress("Buffalo"),
                                             new DummyAddress("London")]]),
                      create(["NY": [new DummyAddress("City")]]),
                      create(["NY": [new DummyAddress("City")]])
        ]
        expectedContainerChanges << [2, 2, 1, 2]
    }

    def "should not support Map<ValueObject,?>"() {
        given:
        def left = new SnapshotEntity(multimapValueObjectToValueObject: HashMultimap.create(create([(new DummyAddress("City")): [new DummyAddress("City")]])))
        def right = new SnapshotEntity()

        when:
        javers.compare(left, right)

        then:
        def e = thrown(JaversException)
        e.code == VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY

    }

    @Unroll
    def "should not detect changes if Multimaps of primitives are the same"() {
        given:
        def left = new SnapshotEntity(multiMapOfPrimitives: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multiMapOfPrimitives: HashMultimap.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 0

        where:
        leftList << [
                     create(["New York" : ["Buffalo"]]),
                     create(["New York" : [
                             "Buffalo", "City"], "London" : ["City"]]),
                     HashMultimap.create()]
       rightList << [create(["New York" : ["Buffalo"]]),
                     create(["New York" : ["Buffalo", "City"], "London" : ["City"]]),
                     HashMultimap.create()]
    }

    @Unroll
    def "should not detect changes if Multimaps of ValueObjects are the same"() {
        given:
        def left = new SnapshotEntity(multimapPrimitiveToValueObject: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multimapPrimitiveToValueObject: HashMultimap.create(rightList))

        when:
        def diff = this.javers.compare(left, right)

        then:
        diff.changes.size() == 0

        where:
        leftList <<  [create([:]),
                      create(["NY" : [new DummyAddress("City"),
                                              new DummyAddress("Buffalo"),
                                              new DummyAddress("London")]])]
        rightList << [create([:]),
                      create(["NY" : [new DummyAddress("City"),
                                              new DummyAddress("Buffalo"),
                                              new DummyAddress("London")]])]
    }

    def "should detect changes in Multimap of Entities"() {
        given:
        def left = new SnapshotEntity(multiMapPrimitiveToEntity: leftList)
        def right = new SnapshotEntity(multiMapPrimitiveToEntity:rightList)

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 1
        diff.changes[0] instanceof MapChange
        diff.changes[0].changes.size() == 1

        def object = diff.changes[0].changes[0]
        with(object){
            it instanceof EntryAdded
            it.value.value() == SnapshotEntity.name + '/' + 2
        }

        where:
        leftList <<  [ create(["NY": [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]]) ]
        rightList << [ create(["NY": [new SnapshotEntity(id:2), new SnapshotEntity(id:2), new SnapshotEntity(id:3)]]) ]
    }

    def "should follow Entities stored in Multimap when building ObjectGraph"(){
        given:
        def left = new SnapshotEntity(multiMapPrimitiveToEntity: leftList)
        def right = new SnapshotEntity(multiMapPrimitiveToEntity: rightList)

        when:
        def diff = javers.compare(left, right)
        println diff.prettyPrint()

        then:
        diff.changes.size() == 2
        diff.getChangesByType(MapChange).size == 1
        diff.getChangesByType(NewObject).size == 1

        where:
        leftList << [ create(["NY": [new SnapshotEntity(id:3)]]) ]
        rightList <<  [ create(["NY": [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]]) ]
    }


    private static Collection<Change> getContainerChanges(final Collection<Change> changes) {
        changes.findAll { it instanceof MapChange || it instanceof SetChange }
    }
}
