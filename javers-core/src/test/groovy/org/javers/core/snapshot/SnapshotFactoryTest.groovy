package org.javers.core.snapshot

import org.javers.core.JaversTestBuilder
import org.javers.core.json.builder.GlobalCdoIdTestBuilder
import org.javers.core.metamodel.object.Cdo
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.property.Property
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserWithValues
import org.javers.test.builder.DummyUserBuilder
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.json.builder.EntityTestBuilder.entity
import static org.javers.core.json.builder.EntityTestBuilder.valueObject
import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class SnapshotFactoryTest extends Specification{

    def "should create snapshot with given GlobalId"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory
        def user = DummyUserBuilder.dummyUser("kaz").build()
        InstanceId id = instanceId(user)

        when:
        CdoSnapshot snapshot = snapshotFactory.create(user, id)

        then:
        snapshot.globalId == id
    }

    def "should record Primitive property"() {
        given:
        SnapshotFactory snapshotFactory = JaversTestBuilder.javersTestAssembly().snapshotFactory
        def user = DummyUserBuilder.dummyUser("kaz").withAge(5).build()

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
}
