package org.javers.core.snapshot

import org.javers.common.collections.Arrays
import org.javers.core.JaversTestBuilder
import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import org.javers.core.graph.LiveNode
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.model.DummyAddress
import org.javers.core.model.PrimitiveEntity
import org.javers.core.model.SnapshotEntity
import java.time.LocalDate
import java.time.LocalDateTime
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.GlobalIdTestBuilder.instanceId
import static org.javers.core.GlobalIdTestBuilder.valueObjectId

/**
 * @author bartosz walacik
 */
class SnapshotFactoryTest extends Specification{

    @Shared JaversTestBuilder javers
    @Shared SnapshotFactory snapshotFactory

    def setupSpec(){
        javers = JaversTestBuilder.javersTestAssembly()
        snapshotFactory = javers.snapshotFactory
    }

    def "should mark all not-null properties as changed of initial snapshot"() {
        given:
        def cdo = new SnapshotEntity(id:1, arrayOfIntegers:[1], arrayOfInts:[1])
        def node = javers.createLiveNode(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.changed as Set == ["id", "arrayOfIntegers", "arrayOfInts"] as Set
    }

    def "should mark nullified properties of update snapshot"() {
        given:
        def cdo = new SnapshotEntity(id:1, arrayOfIntegers:[1])
        def node = javers.createLiveNode(cdo)
        def prevSnapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        when:
        cdo.arrayOfIntegers = null
        def updateSnapshot = snapshotFactory.createUpdate(node, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.changed == ["arrayOfIntegers"]
    }

    def "should update the version of the snapshot"() {
        given:
        def node = javers.createLiveNode(new SnapshotEntity(id: 1))
        def prevSnapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        when:
        def updateSnapshot = snapshotFactory.createUpdate(node, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.version == 2L
    }

    def "should initialize the version of the first snapshot"() {
        given:
        def node = javers.createLiveNode(new SnapshotEntity(id: 1))

        when:
        def snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.version == 1L
    }

    def "should mark changed and added properties of update snapshot"() {
        given:
        def ref = new SnapshotEntity(id:2)
        def cdo = new SnapshotEntity(id:1, arrayOfIntegers:[1,1], arrayOfInts:[5,5], entityRef: ref)
        def node = javers.createLiveNode(cdo)

        def prevSnapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        when:
        cdo.arrayOfIntegers[0] = 2
        cdo.arrayOfInts[0] = 2

        def updateSnapshot1 = snapshotFactory.createUpdate(node, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot1.changed as Set == ["arrayOfIntegers","arrayOfInts"] as Set

        when:
        prevSnapshot = updateSnapshot1
        cdo.entityRef = new SnapshotEntity(id:3)
        node = javers.createLiveNode(cdo)
        def updateSnapshot2 = snapshotFactory.createUpdate(node, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot2.changed == ["entityRef"]

        when:
        prevSnapshot = updateSnapshot2
        cdo.dob = LocalDate.now()
        def updateSnapshot3 = snapshotFactory.createUpdate(node, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot3.changed == ["dob"]
    }

    def "should create snapshot with given GlobalId"() {
        given:
        def cdo = new SnapshotEntity(id:1)
        def node = javers.createLiveNode(cdo)
        def id = javers.instanceId(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.globalId == id
    }

    def "should skip primitives with default value"() {
        given:
        def cdo = new PrimitiveEntity()
        def node = javers.createLiveNode(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.size() == 1
    }

    @Unroll
    def "should record #propertyType property value"() {
        when:
        def node = javers.createLiveNode(cdo)
        def snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.getPropertyValue(propertyName) == cdo.getAt(propertyName)

        where:
        propertyType << ["Primitive", "Value"]
        propertyName << ["intProperty","dob"]
        cdo <<          [new SnapshotEntity(intProperty: 5),
                         new SnapshotEntity(dob: new LocalDate(2000,1,1))]
    }

    @Unroll
    def "should record #propertyType reference"() {
        when:
        def node = javers.createLiveNode(cdo)
        def snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal

        where:
        propertyType << ["Entity","ValueObject"]
        propertyName << ["entityRef", "valueObjectRef"]
        cdo <<          [new SnapshotEntity(id:1, entityRef:new SnapshotEntity(id:5)),
                         new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("street"))]
        expectedVal <<  [instanceId(5, SnapshotEntity),
                         valueObjectId(1, SnapshotEntity, "valueObjectRef")]
    }

    def "should record empty Optional"(){
        given:
        def cdo = new SnapshotEntity(optionalInteger: Optional.empty())
        def node = javers.createLiveNode(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.getPropertyValue("optionalInteger") == Optional.empty()
    }

    @Unroll
    def "should record Optional of #propertyType"(){
        when:
        def node = javers.createLiveNode(cdo)
        def snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal

        where:
        propertyType <<  ["Primitive", "Value", "Entity", "ValueObject"]
        propertyName <<  ["optionalInteger", "optionalDate", "optionalEntity", "optionalValueObject"]
        cdo << [new SnapshotEntity(optionalInteger: Optional.of(1)),
                new SnapshotEntity(optionalDate: Optional.of(new LocalDate(2000, 1, 1))),
                new SnapshotEntity(optionalEntity: Optional.of(new SnapshotEntity(id:5))),
                new SnapshotEntity(optionalValueObject: Optional.of(new DummyAddress("London")))
        ]
        expectedVal <<[
                Optional.of(1),
                Optional.of(new LocalDate(2000, 1, 1)),
                Optional.of(instanceId(5, SnapshotEntity)),
                Optional.of(valueObjectId(1, SnapshotEntity,"optionalValueObject"))
        ]
    }


    @Unroll
    def "should record #containerType of #propertyType"() {
        when:
        def node = javers.createLiveNode(cdo)
        def snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal

        expectedType.isAssignableFrom(snapshot.getPropertyValue(propertyName).class)
        //we need copy
        System.identityHashCode(snapshot.getPropertyValue(propertyName)) !=
        System.identityHashCode( cdo.getAt(propertyName) )


        where:
        expectedType  <<  [Arrays.INT_ARRAY_TYPE] + [Arrays.OBJECT_ARRAY_TYPE]*4 +[List]*4
        containerType <<  ["Array"]*5 +["List"]*4
        propertyType  <<  ["primitive"] + ["Primitive", "Value", "Entity", "ValueObject"]*2
        propertyName  <<  ["arrayOfInts", "arrayOfIntegers", "arrayOfDates", "arrayOfEntities", "arrayOfValueObjects",
                           "listOfIntegers",  "listOfDates",  "listOfEntities",  "listOfValueObjects"]
        cdo << [
                new SnapshotEntity(arrayOfInts:         [1, 2]),
                new SnapshotEntity(arrayOfIntegers:     [1, 2]),
                new SnapshotEntity(arrayOfDates:        [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)]),
                new SnapshotEntity(arrayOfEntities:     [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]),
                new SnapshotEntity(arrayOfValueObjects: [new DummyAddress("London"), new DummyAddress("London City")]),
                new SnapshotEntity(listOfIntegers:      [1, 2]),
                new SnapshotEntity(listOfDates:         [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)]),
                new SnapshotEntity(listOfEntities:      [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]),
                new SnapshotEntity(listOfValueObjects:  [new DummyAddress("London"), new DummyAddress("London City")])
        ]
        expectedVal << [
                [1, 2],
                [1, 2],
                [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)],
                [instanceId(2, SnapshotEntity), instanceId(3, SnapshotEntity)],
                [valueObjectId(1, SnapshotEntity,"arrayOfValueObjects/0"),
                 valueObjectId(1, SnapshotEntity,"arrayOfValueObjects/1")] ,
                [1, 2],
                [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)],
                [instanceId(2, SnapshotEntity), instanceId(3, SnapshotEntity)],
                [valueObjectId(1, SnapshotEntity,"listOfValueObjects/0"),
                 valueObjectId(1, SnapshotEntity,"listOfValueObjects/1")]
        ]
    }

    @Unroll
    def "should record Set of #propertyType"() {
        given:
        def node = javers.createLiveGraph(cdo).root()

        when:
        CdoSnapshot snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal
        //we need shallow copy
        System.identityHashCode(snapshot.getPropertyValue(propertyName)) != System.identityHashCode(cdo.getAt(propertyName))

        where:
        propertyType << ["Primitive", "Value", "Entity", "ValueObject"]
        propertyName << ["setOfIntegers", "setOfDates", "setOfEntities", "setOfValueObjects"]
        cdo << [
                new SnapshotEntity(setOfIntegers:     [1, 2]),
                new SnapshotEntity(setOfDates:        [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)]),
                new SnapshotEntity(setOfEntities:     [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]),
                new SnapshotEntity(setOfValueObjects: [new DummyAddress("London"), new DummyAddress("London City")])
        ]
        expectedVal << [
                [1, 2] as Set,
                [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)] as Set,
                [instanceId(2, SnapshotEntity), instanceId(3, SnapshotEntity)] as Set,
                [valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("London")),
                 valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("London City"))] as Set
        ]
    }

    def "should handle property with not parametrized type"() {
        when:
        def cdo = new SnapshotEntity(nonParametrizedMap:  ["a":1])
        def node = javers.createLiveNode(cdo)
        def snap = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snap.getPropertyValue("nonParametrizedMap") == ["a":1]
    }

    @Unroll
    def "should record Map of #enrtyType"() {
        when:
        def node = javers.createLiveNode(cdo)
        CdoSnapshot snapshot = snapshotFactory.createInitial(node, someCommitMetadata())

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal
        //we need shallow copy
        System.identityHashCode(snapshot.getPropertyValue(propertyName)) != System.identityHashCode(cdo.getAt(propertyName))

        where:
        enrtyType <<    ["<Primitive,Primitive>", "<Value,Value>", "<Primitive,ValueObject>", "<Entity,Entity>"]
        propertyName << ["mapOfPrimitives",       "mapOfValues",   "mapPrimitiveToVO",        "mapOfEntities"]
        cdo << [new SnapshotEntity(mapOfPrimitives:  ["this":1,"that":2]),
                new SnapshotEntity(mapOfValues:      [(new LocalDate(2000, 1, 1)):1.5]),
                new SnapshotEntity(mapPrimitiveToVO: ["key1":new DummyAddress("London")]),
                new SnapshotEntity(mapOfEntities:    [(new SnapshotEntity(id:2)):new SnapshotEntity(id:3)])
        ]

        expectedVal << [
                        ["this":1,"that":2],
                        [(new LocalDate(2000, 1, 1)):1.5],
                        ["key1":valueObjectId(1, SnapshotEntity,"mapPrimitiveToVO/key1")],
                        [(javers.instanceId(2,SnapshotEntity)):
                          javers.instanceId(3,SnapshotEntity)]
                       ]
    }

    def someCommitMetadata(){
        new CommitMetadata("kazik", [:], LocalDateTime.now(), null, new CommitId(1, 0))
    }
}
