package org.javers.core.snapshot

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

import static JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId
import static org.javers.core.metamodel.object.ValueObjectIdDTO.valueObjectId

/**
 * @author bartosz walacik
 */
class SnapshotFactoryTest extends Specification{

    @Shared JaversTestBuilder javers

    def setupSpec(){
        javers = JaversTestBuilder.javersTestAssembly()
    }

    def "should create snapshot with given GlobalId"() {
        given:
        def snapshotFactory = javers.snapshotFactory
        def cdo = new SnapshotEntity(id:1)
        def id = javers.instanceId(cdo)

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, id, new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0)))

        then:
        snapshot.globalId == id
    }

    def "should skip primitives with default value"() {
        given:
        def snapshotFactory = javers.snapshotFactory
        def cdo = new PrimitiveEntity()
        def id = javers.instanceId(cdo)

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, id, new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0)))

        then:
        snapshot.size() == 0
    }

    @Unroll
    def "should record #propertyType property value"() {
        given:
        def snapshotFactory = javers.snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, javers.instanceId(cdo),
                new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0)))

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
        given:
        def snapshotFactory = javers.snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, javers.instanceId(cdo),
                new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0)))

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
        given:
        def snapshotFactory = javers.snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, javers.instanceId(cdo),
                new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0)))

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
        given:
        def snapshotFactory = javers.snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, javers.instanceId(cdo),
                new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0)))

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

    def "should not support Map of <ValueObject,?>, no good idea how to handle this"() {
        given:
        def snapshotFactory = javers.snapshotFactory

        when:
        def cdo = new SnapshotEntity(mapVoToPrimitive:  [(new DummyAddress("London")):"this"])
        snapshotFactory.create(cdo, javers.instanceId(cdo), new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0)))

        then:
        def e = thrown(JaversException)
        e.code == VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
    }

    def "should throw exception when property Type is not fully parametrized"() {
        given:
        def snapshotFactory = javers.snapshotFactory

        when:
        def cdo = new SnapshotEntity(nonParametrizedMap:  ["a":1])
        snapshotFactory.create(cdo, javers.instanceId(cdo), new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0)))

        then:
        def e = thrown(JaversException)
        e.code == JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
    }

    @Unroll
    def "should record Map of #enrtyType"() {
        given:
        def snapshotFactory = javers.snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, javers.instanceId(cdo),
                new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0)))

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
}
