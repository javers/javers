package org.javers.core.snapshot

import org.javers.common.collections.Arrays
import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.JaversTestBuilder
import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import org.javers.core.metamodel.object.CdoSnapshot
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

    def setupSpec(){
        javers = JaversTestBuilder.javersTestAssembly()
        snapshotFactory = javers.snapshotFactory
    }

    def "should mark all not-nul properties as changed of initial snapshot"() {
        given:
        def cdo = new SnapshotEntity(id:1, arrayOfIntegers:[1], arrayOfInts:[1])
        def cdoWrapper = javers.createCdoWrapper(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

        then:
        snapshot.changed as Set == ["id","arrayOfIntegers", "arrayOfInts"] as Set
    }

    def "should mark nullified properties of update snapshot"() {
        given:
        def cdo = new SnapshotEntity(id:1, arrayOfIntegers:[1])
        def cdoWrapper = javers.createCdoWrapper(cdo)
        def prevSnapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

        when:
        cdo.arrayOfIntegers = null
        def updateSnapshot = snapshotFactory.createUpdate(cdoWrapper, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.changed == ["arrayOfIntegers"]
    }

    def "should update the version of the snapshot"() {
        given:
        def cdoWrapper = javers.createCdoWrapper(new SnapshotEntity(id: 1))
        def prevSnapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

        when:
        def updateSnapshot = snapshotFactory.createUpdate(cdoWrapper, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.version == 2L
    }

    def "should initialize the version of the first snapshot"() {
        given:
        def cdoWrapper = javers.createCdoWrapper(new SnapshotEntity(id: 1))

        when:
        def snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

        then:
        snapshot.version == 1L
    }

    def "should mark changed and added properties of update snapshot"() {
        given:
        def ref = new SnapshotEntity(id:2)
        def cdo = new SnapshotEntity(id:1, arrayOfIntegers:[1,1], arrayOfInts:[5,5], entityRef: ref)
        def cdoWrapper = javers.createCdoWrapper(cdo)

        def prevSnapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

        when:
        cdo.arrayOfIntegers[0] = 2
        cdo.arrayOfInts[0] = 2
        def updateSnapshot = snapshotFactory.createUpdate(cdoWrapper, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.changed as Set == ["arrayOfIntegers","arrayOfInts"] as Set

        when:
        prevSnapshot = updateSnapshot
        cdo.entityRef = new SnapshotEntity(id:3)
        updateSnapshot = snapshotFactory.createUpdate(cdoWrapper, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.changed == ["entityRef"]

        when:
        prevSnapshot = updateSnapshot
        cdo.dob = new LocalDate()
        updateSnapshot = snapshotFactory.createUpdate(cdoWrapper, prevSnapshot, someCommitMetadata())

        then:
        updateSnapshot.changed == ["dob"]
    }

    def "should create snapshot with given GlobalId"() {
        given:
        def cdo = new SnapshotEntity(id:1)
        def cdoWrapper = javers.createCdoWrapper(cdo)
        def id = javers.instanceId(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

        then:
        snapshot.globalId == id
    }

    def "should skip primitives with default value"() {
        given:
        def cdo = new PrimitiveEntity()
        def cdoWrapper = javers.createCdoWrapper(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

        then:
        snapshot.size() == 0
    }

    @Unroll
    def "should record #propertyType property value"() {
        when:
        def cdoWrapper = javers.createCdoWrapper(cdo)
        def snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

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
        def cdoWrapper = javers.createCdoWrapper(cdo)
        def snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

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
        def cdoWrapper = javers.createCdoWrapper(cdo)

        when:
        def snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

        then:
        snapshot.getPropertyValue("optionalInteger") == Optional.empty()
    }

    @Unroll
    def "should record Optional of #propertyType"(){
        when:
        def cdoWrapper = javers.createCdoWrapper(cdo)
        def snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

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
        def cdoWrapper = javers.createCdoWrapper(cdo)
        def snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

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
        def cdoWrapper = javers.createCdoWrapper(cdo)

        when:
        CdoSnapshot snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

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
                        [valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("London")),
                         valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("London City"))] as Set
                       ]
    }

    def "should throw exception when property Type is not fully parametrized"() {
        when:
        def cdo = new SnapshotEntity(nonParametrizedMap:  ["a":1])
        def cdoWrapper = javers.createCdoWrapper(cdo)
        snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

        then:
        def e = thrown(JaversException)
        e.code == JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
    }

    @Unroll
    def "should record Map of #enrtyType"() {
        when:
        def cdoWrapper = javers.createCdoWrapper(cdo)
        CdoSnapshot snapshot = snapshotFactory.createInitial(cdoWrapper, someCommitMetadata())

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
        new CommitMetadata("kazik", [:], LocalDateTime.now(), new CommitId(1, 0))
    }
}
