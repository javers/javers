package org.javers.core.snapshot

import org.javers.common.exception.exceptions.JaversException
import org.javers.common.exception.exceptions.JaversExceptionCode
import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.common.exception.exceptions.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.instanceId
import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.*

/**
 * @author bartosz walacik
 */
class SnapshotFactoryTest extends Specification{

    def "should create snapshot with given GlobalId"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory
        def cdo = new SnapshotEntity()
        InstanceId id = instanceId(cdo)

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, id)

        then:
        snapshot.globalId == id
    }

    @Unroll
    def "should record #propertyType property value"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, instanceId(cdo))

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
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, instanceId(cdo))

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal

        where:
        propertyType << ["Entity","ValueObject"]
        propertyName << ["entityRef", "valueObjectRef"]
        cdo <<          [new SnapshotEntity(id:1, entityRef:new SnapshotEntity(id:5)),
                         new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("street"))]
        expectedVal <<  [instanceId(5, SnapshotEntity),
                         valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"valueObjectRef")]
    }

    @Unroll
    def "should record Array of #propertyType"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, instanceId(cdo))

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal
        //we need shallow copy
        snapshot.getPropertyValue(propertyName).hashCode() != cdo.getAt(propertyName).hashCode()

        where:
        propertyType << ["Primitive", "Value", "Entity", "ValueObject"]
        propertyName << ["arrayOfIntegers", "arrayOfDates", "arrayOfEntities", "arrayOfValueObjects"]
        cdo << [new SnapshotEntity(arrayOfIntegers:     [1, 2]),
                new SnapshotEntity(arrayOfDates:        [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)]),
                new SnapshotEntity(arrayOfEntities:     [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]),
                new SnapshotEntity(arrayOfValueObjects: [new DummyAddress("London"), new DummyAddress("London City")])
               ]
        expectedVal << [[1, 2],
                        [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)],
                        [instanceId(2, SnapshotEntity), instanceId(3, SnapshotEntity)],
                        [valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"arrayOfValueObjects/0"),
                         valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"arrayOfValueObjects/1")]
                       ]
    }

    @Unroll
    def "should record Set of #propertyType"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, instanceId(cdo))

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
                         valueObjectSetId(instanceId(1, SnapshotEntity),DummyAddress,"setOfValueObjects")
                       ]
    }

    def "should not support Map of <ValueObject,?>"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory

        when:
        def cdo = new SnapshotEntity(mapVoToPrimitive:  [(new DummyAddress("London")):"this"])
        snapshotFactory.create(cdo, instanceId(cdo))

        then:
        def e = thrown(JaversException)
        e.code == VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY;
    }

    def "should throw exception when property Type is not fully parametrized"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory

        when:
        def cdo = new SnapshotEntity(nonParametrizedMap:  ["a":1])
        snapshotFactory.create(cdo, instanceId(cdo))

        then:
        def e = thrown(JaversException)
        e.code == JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
    }

    @Unroll
    def "should record Map of #enrtyType"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(cdo, instanceId(cdo))

        then:
        snapshot.getPropertyValue(propertyName) == expectedVal
        //we need shallow copy
        System.identityHashCode(snapshot.getPropertyValue(propertyName)) != System.identityHashCode(cdo.getAt(propertyName))

        println(expectedVal)

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
                        ["key1":valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"mapPrimitiveToVO/key1")],
                        [(instanceId(2, SnapshotEntity)): instanceId(3, SnapshotEntity)]
                       ]
    }
}
