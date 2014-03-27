package org.javers.core.snapshot

import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.instanceId
import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.valueObjectId

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
        cdo << [new SnapshotEntity(arrayOfIntegers: [1, 2]),
                new SnapshotEntity(arrayOfDates: [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)]),
                new SnapshotEntity(arrayOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]),
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
    def "should record List of #propertyType"() {
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
        propertyName << ["listOfIntegers", "listOfDates", "listOfEntities", "listOfValueObjects"]
        cdo << [new SnapshotEntity(listOfIntegers: [1, 2]),
                new SnapshotEntity(listOfDates: [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)]),
                new SnapshotEntity(listOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]),
                new SnapshotEntity(listOfValueObjects: [new DummyAddress("London"), new DummyAddress("London City")])
        ]
        expectedVal << [[1, 2],
                        [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)],
                        [instanceId(2, SnapshotEntity), instanceId(3, SnapshotEntity)],
                        [valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"listOfValueObjects/0"),
                         valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"listOfValueObjects/1")]
        ]
    }
}
