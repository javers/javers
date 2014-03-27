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
        snapshot.getPropertyValue(propertyName) == cdo.getAt(propertyName)
        //we need shallow copy
        snapshot.getPropertyValue(propertyName).hashCode() != cdo.getAt(propertyName).hashCode()

        where:
        propertyType << ["Primitive", "Value"]
        propertyName << ["arrayOfIntegers", "arrayOfDates"]
        cdo << [new SnapshotEntity(arrayOfIntegers: [1, 2]),
                new SnapshotEntity(arrayOfDates: [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)])]
        //expectedVal << [[1, 2],
        //                [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)]]
    }
}
