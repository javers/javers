package org.javers.core.metamodel.object


import org.javers.core.model.*
import org.javers.repository.jql.ValueObjectIdDTO
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz.walacik
 */
class GlobalIdFactoryTest extends Specification {

    @Shared
    GlobalIdFactory globalIdFactory = javersTestAssembly().globalIdFactory

    def "should build instanceId using reflectiveToString() for Embedded Id "() {
        when:
        def instanceId = globalIdFactory.createId(
                new DummyEntityWithEmbeddedId(point: new DummyPoint(1,3)))

        then:
        instanceId.typeName == DummyEntityWithEmbeddedId.name
        instanceId.cdoId instanceof DummyPoint
        instanceId.cdoId.x == 1
        instanceId.cdoId.y == 3
        instanceId.value() == DummyEntityWithEmbeddedId.name + "/1,3"
    }

    def "should create proper InstanceId for Instance with Composite Id"(){
        given:
        def person = new PersonComposite(name: "mad", surname: "kaz", dob: LocalDate.of(2019,01,01), data: 1)

        when:
        def instanceId = globalIdFactory.createId(person)

        then:
        instanceId.typeName == PersonComposite.name
        instanceId.cdoId == "2019,1,1,mad,kaz"
        instanceId.value() == PersonComposite.name + "/2019,1,1,mad,kaz"
    }

    def "should create proper InstanceId for simple EntityId case with delegated cdoId"(){
        given:
        def person = new PersonSimpleEntityId(personId: new PersonId(name: "mad", id: 10), data: 1)

        when:
        def instanceId = globalIdFactory.createId(person)

        then:
        instanceId.typeName == PersonSimpleEntityId.name
        instanceId.cdoId == 10
        instanceId.value() == PersonSimpleEntityId.name + "/10"
    }

    def "should create proper InstanceId for Composite EntityId case with joined, delegated cdoId"(){
        given:
        def person = new PersonCompositeEntityId(
                firstNameId: new FirstNameId(name: "mad", id:10),
                lastNameId: new LastNameId(name: "kaz", id:11),
                data: 1)

        when:
        def instanceId = globalIdFactory.createId(person)

        then:
        instanceId.typeName == PersonCompositeEntityId.name
        instanceId.cdoId == "10,11"
        instanceId.value() == PersonCompositeEntityId.name + "/10,11"
    }

    @Unroll
    def "should infer valueObjectType from path when path is #pathType"(){
      when:
      def id = globalIdFactory.createFromDto(ValueObjectIdDTO.valueObjectId(1, SnapshotEntity, path))

      then:
      id.typeName == DummyAddress.name

      where:
      path <<     ["valueObjectRef",  "mapPrimitiveToVO/HOME", "listOfValueObjects/0"]
      pathType << ["simple property", "map.property/map.key",  "list.property/list.index"]
    }

    @Unroll
    def "should parse valueObjectType from path for embedded ValueObject (#pathType)"() {
        when:
        def id = globalIdFactory.createFromDto(ValueObjectIdDTO.valueObjectId(1, SnapshotEntity, path))

        then:
        id.typeName == DummyNetworkAddress.name

        where:
        path <<     ["valueObjectRef/networkAddress",
                     "listOfValueObjects/0/networkAddress",
                     "mapPrimitiveToVO/HOME/networkAddress"]
        pathType << ["property/property", "list.property/list.index/property", "map.property/map.key/property"]
    }
}
