package org.javers.core.snapshot

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.JaversTestBuilder
import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.SnapshotFactory
import org.javers.core.model.DummyAddress
import org.javers.core.model.PrimitiveEntity
import org.javers.core.model.SnapshotEntity
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.repository.jql.ValueObjectIdDTO.valueObjectId

/**
 * @author bartosz walacik
 */
class SnapshotFactoryTest extends Specification{

    @Shared JaversTestBuilder javers
    @Shared SnapshotFactory snapshotFactory

    final String = "1"

    def setupSpec(){
        javers = JaversTestBuilder.javersTestAssembly()
        snapshotFactory = javers.snapshotFactory
    }

    def "should mark all not-nul properties as changed for initial snapshot"() {
        given:
        def cdo = new SnapshotEntity(id:1, arrayOfIntegers:[1])
        def id = javers.instanceId(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(cdo, id, someCommitMetadata())

        then:
        snapshot.changed.collect{it.name} as Set == ["id","arrayOfIntegers"] as Set
    }

    def "should mark changed and added properties for update snapshot"() {
        given:
        def ref = new SnapshotEntity(id:2)
        def cdo = new SnapshotEntity(id:1, arrayOfIntegers:[1], entityRef: ref)
        def id = javers.instanceId(cdo)

        def prevSnapshot = snapshotFactory.createInitial(cdo, id, someCommitMetadata())

        when:
        cdo.arrayOfIntegers[0] = 2
        def updateSnapshot = snapshotFactory.createUpdate(cdo, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.changed.collect{it.name} == ["arrayOfIntegers"]

        when:
        prevSnapshot = updateSnapshot
        cdo.entityRef = new SnapshotEntity(id:3)
        updateSnapshot = snapshotFactory.createUpdate(cdo, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.changed.collect{it.name} == ["entityRef"]

        when:
        prevSnapshot = updateSnapshot
        cdo.dob = new LocalDate()
        updateSnapshot = snapshotFactory.createUpdate(cdo, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.changed.collect{it.name} == ["dob"]
    }

    def "should create snapshot with given GlobalId"() {
        given:
        def cdo = new SnapshotEntity(id:1)
        def id = javers.instanceId(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(cdo, id, someCommitMetadata())

        then:
        snapshot.globalId == id
    }

    def "should skip primitives with default value"() {
        given:
        def cdo = new PrimitiveEntity()
        def id = javers.instanceId(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(cdo, id, someCommitMetadata())

        then:
        snapshot.size() == 0
    }

    @Unroll
    def "should record #propertyType property value"() {
        when:
        CdoSnapshot snapshot = snapshotFactory.createInitial(cdo, javers.instanceId(cdo), someCommitMetadata())

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
        CdoSnapshot snapshot = snapshotFactory.createInitial(cdo, javers.instanceId(cdo), someCommitMetadata())

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


    @Unroll
    def "should record #containerType of #propertyType"() {
        when:
        CdoSnapshot snapshot = snapshotFactory.createInitial(cdo, javers.instanceId(cdo), someCommitMetadata())

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal
        //we need shallow copy
        System.identityHashCode(snapshot.getPropertyValue(propertyName)) !=
        System.identityHashCode( cdo.getAt(propertyName) )


        where:
        containerType << ["Array"]*4 +["List"]*4
        propertyType <<  ["Primitive", "Value", "Entity", "ValueObject"]*2
        propertyName <<  ["arrayOfIntegers", "arrayOfDates", "arrayOfEntities", "arrayOfValueObjects",
                         "listOfIntegers",  "listOfDates",  "listOfEntities",  "listOfValueObjects"]
        cdo << [
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
        when:
        CdoSnapshot snapshot = snapshotFactory.createInitial(cdo, javers.instanceId(cdo), someCommitMetadata())

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal
        //we need shallow copy
        System.identityHashCode(snapshot.getPropertyValue(propertyName)) != System.identityHashCode(cdo.getAt(propertyName))

        where:
        propertyType << ["Primitive", "Value", "Entity", "ValueObject"]
        propertyName << ["setOfIntegers", "setOfDates", "setOfEntities", "setOfValueObjects"]
        cdo << [new SnapshotEntity(setOfIntegers:     [1, 2]),
                new SnapshotEntity(setOfDates:        [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)]),
                new SnapshotEntity(setOfEntities:     [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]),
                new SnapshotEntity(setOfValueObjects: [new DummyAddress("London"), new DummyAddress("London City")])
        ]
        expectedVal << [[1, 2] as Set,
                        [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)] as Set,
                        [instanceId(2, SnapshotEntity), instanceId(3, SnapshotEntity)] as Set,
                        [valueObjectId(1, SnapshotEntity, "setOfValueObjects/random_0"),
                         valueObjectId(1, SnapshotEntity, "setOfValueObjects/random_1")] as Set
                       ]
    }

    def "should throw exception when property Type is not fully parametrized"() {
        when:
        def cdo = new SnapshotEntity(nonParametrizedMap:  ["a":1])
        snapshotFactory.createInitial(cdo, javers.instanceId(cdo), someCommitMetadata())

        then:
        def e = thrown(JaversException)
        e.code == JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
    }

    @Unroll
    def "should record Map of #enrtyType"() {
        when:
        CdoSnapshot snapshot = snapshotFactory.createInitial(cdo, javers.instanceId(cdo), someCommitMetadata())

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
                        [(javers.idBuilder().instanceId(2,SnapshotEntity)):
                          javers.idBuilder().instanceId(3,SnapshotEntity)]
                       ]
    }

    def someCommitMetadata(){
        new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0))
    }
}
