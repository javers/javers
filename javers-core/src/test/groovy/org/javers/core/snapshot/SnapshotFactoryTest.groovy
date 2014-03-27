package org.javers.core.snapshot

import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.DummyUserWithValues
import org.joda.time.LocalDateTime
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.instanceId
import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.valueObjectId
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails

/**
 * @author bartosz walacik
 */
class SnapshotFactoryTest extends Specification{

    def "should create snapshot with given GlobalId"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory
        def user = dummyUser("kaz").build()
        InstanceId id = instanceId(user)

        when:
        CdoSnapshot snapshot = snapshotFactory.create(user, id)

        then:
        snapshot.globalId == id
    }

    def "should record Primitive property"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory
        def user = dummyUser("kaz").withAge(5).build()

        when:
        CdoSnapshot snapshot = snapshotFactory.create(user, instanceId(user))

        then:
        snapshot.getPropertyValue("age") == 5
    }

    def "should record Value property"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory
        def dob = new LocalDateTime()
        def user = new DummyUserWithValues("kaz",dob)

        when:
        CdoSnapshot snapshot = snapshotFactory.create(user, instanceId(user))

        then:
        snapshot.getPropertyValue("dob") == dob
    }

    @Unroll
    def "should record #refType reference"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory

        when:
        CdoSnapshot snapshot = snapshotFactory.create(user, instanceId(user))

        then:
        snapshot.getPropertyValue(propertyName) == expectedId

        where:
        user << [dummyUser("kaz").withDetails(5).build(),
                 dummyUserDetails(1).withAddress("street","city").build()]
        propertyName <<  ["dummyUserDetails",
                          "dummyAddress"]
        refType << ["Entity",
                    "ValueObject"]
        expectedId << [instanceId(5L, DummyUserDetails),
                       valueObjectId(instanceId(1L, DummyUserDetails),DummyAddress,"dummyAddress")]

    }
}
