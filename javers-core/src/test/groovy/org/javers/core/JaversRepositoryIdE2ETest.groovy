package org.javers.core


import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.ValueObject
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.metamodel.type.ValueType
import org.javers.core.model.CategoryC
import org.javers.core.model.DummyEntityWithEmbeddedId
import org.javers.core.model.DummyPoint
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate


class JaversRepositoryIdE2ETest extends Specification {
    @Shared Javers javers = JaversBuilder.javers().build()

    def "should support Value as Entity Id"(){
        given:
        def cdo  = new DummyEntityWithEmbeddedId(point: new DummyPoint(1,2), someVal: 5)

        when:
        javers.commit("author", cdo)

        then:
        javers.getTypeMapping(DummyPoint) instanceof ValueType

        def snapshot = javers.getLatestSnapshot(new DummyPoint(1,2), DummyEntityWithEmbeddedId).get()
        snapshot.globalId.value().endsWith("DummyEntityWithEmbeddedId/1,2")
        snapshot.globalId.cdoId == new DummyPoint(1,2)
    }

    def "should support long numbers as Entity Id "(){
        given:
        def longId = 1000000000L*1000
        def category = new CategoryC(longId)

        when:
        javers.commit("author",category)

        then:
        javers.getLatestSnapshot(longId, CategoryC).get().globalId.cdoId == longId
    }

    class EntityWithVOId {
        @Id ValueObjectAsId id
        int value
    }

    @ValueObject
    class ValueObjectAsId {
        int id
        int value
    }

    def "should support ValueObject as Id property "(){
        given:
        def first  = new EntityWithVOId(id: new ValueObjectAsId(id: 1, value:5), value:5)
        def second = new EntityWithVOId(id: new ValueObjectAsId(id: 1, value:5), value:6)

        when:
        javers.commit("author", first)
        javers.commit("author", second)
        def snapshot = javers.getLatestSnapshot(new ValueObjectAsId(id: 1, value:5), EntityWithVOId).get()

        then:
        snapshot.globalId.value().endsWith("EntityWithVOId/1,5")
        snapshot.globalId.cdoId == "1,5"

        snapshot.getPropertyValue("id") instanceof ValueObjectId
        snapshot.getPropertyValue("id").value().endsWith("EntityWithVOId/1,5#id")
    }

    class DummyWithEntityId {
        @Id EntityAsId entityAsId
        int value
    }

    class EntityAsId {
        @Id
        int id
        int value
    }

    def "should support nested Entity as Id property"(){
        given:
        def first  = new DummyWithEntityId(entityAsId: new EntityAsId(id: 1, value:5), value:5)
        def second = new DummyWithEntityId(entityAsId: new EntityAsId(id: 1, value:5), value:6)

        when:
        javers.commit("author", first)
        javers.commit("author", second)
        def snapshot = javers.getLatestSnapshot(new EntityAsId(id: 1), DummyWithEntityId).get()

        then:

        snapshot.globalId.value().endsWith("DummyWithEntityId/1")
        snapshot.globalId.cdoId == 1
        snapshot.getPropertyValue("entityAsId") instanceof InstanceId
        snapshot.getPropertyValue("entityAsId").value().endsWith("EntityAsId/1")
    }

    class Person {
        @Id String name
        @Id String surname
        @Id LocalDate dob
        int data
    }

    def "should support Composite Id assembled from Values"(){
        given:
        def first  = new Person(name: "mad", surname: "kaz", dob: LocalDate.of(2019,01,01), data: 1)
        def second = new Person(name: "mad", surname: "kaz", dob: LocalDate.of(2019,01,01), data: 2)

        when:
        javers.commit("author", first)
        javers.commit("author", second)
        def snapshot = javers.getLatestSnapshot(
                [
                    name: "mad",
                    surname: "kaz",
                    dob: LocalDate.of(2019,01,01)
                ],
                Person).get()

        then:
        snapshot.globalId.value().endsWith("Person/2019,1,1,mad,kaz")
        snapshot.globalId.cdoId == "2019,1,1,mad,kaz"
        snapshot.getPropertyValue("name") == "mad"
        snapshot.getPropertyValue("surname") == "kaz"
        snapshot.getPropertyValue("dob") == LocalDate.of(2019,01,01)
        snapshot.getPropertyValue("data") == 2
        snapshot.changed == ["data"]
    }
}
