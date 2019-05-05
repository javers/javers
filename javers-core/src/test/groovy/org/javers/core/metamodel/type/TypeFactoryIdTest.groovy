package org.javers.core.metamodel.type

import groovy.transform.MapConstructor
import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyEntityWithEmbeddedId
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

import static org.javers.common.exception.JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_COMPOSITE_ID
import static org.javers.core.metamodel.clazz.EntityDefinitionBuilder.entityDefinition

/**
 * @author bartosz walacik
 */
abstract class TypeFactoryIdTest extends Specification {
    @Shared
    TypeFactory typeFactory

    def "should use @EmbeddedId as Id property"(){
        when:
        def entity = typeFactory.create(new EntityDefinition(DummyEntityWithEmbeddedId))

        then:
        entity.idProperty.name == 'point'
    }

    def "should use @Id property as Id property"() {
        when:
        def entity = typeFactory.create(new EntityDefinition(DummyUser.class))

        then:
        entity.idProperty.name == 'name'
    }

    def "should ignore @Id annotation when idProperty name is given"() {
        when:
        def entity = typeFactory.create(new EntityDefinition(DummyUser,"bigFlag"))

        then:
        entity.idProperty.name == 'bigFlag'
    }

    def "should not ignore @Transient annotation when idProperty name is given"() {
        when:
        typeFactory.create(new EntityDefinition(DummyUser,"propertyWithTransientAnn"))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.PROPERTY_NOT_FOUND
        println(e)
    }

    def "should fail for Entity without Id property"() {
        when:
        typeFactory.create(new EntityDefinition(DummyAddress.class))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.ENTITY_WITHOUT_ID
    }

    def "should fail when given Id property name doesn't exists"() {
        when:
        typeFactory.create(new EntityDefinition(DummyUser,"zonk"))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.PROPERTY_NOT_FOUND
    }

    class PersonNoAnn {
        String name
        String surname
        LocalDate dob
        int data
    }

    def "should support Composite Id declared explicitly in EntityDefinition"() {
        when:
        EntityType entity = typeFactory.create(entityDefinition(PersonNoAnn)
                .withIdPropertyNames('name','surname','dob').build())

        then:
        entity.getIdPropertyNames() as Set == ['name','surname','dob'] as Set
    }

    @MapConstructor
    class Person {
        @Id String name
        @Id String surname
        @Id LocalDate dob
        int data

        @Id String getName() {
            return name
        }

        @Id String getSurname() {
            return surname
        }

        @Id LocalDate getDob() {
            return dob
        }
    }

    def "should support Composite Id mapped with @Id"() {
        when:
        EntityType entity = typeFactory.create(new EntityDefinition(Person))

        then:
        entity.getIdPropertyNames() as Set == ['name','surname','dob'] as Set
    }

    def "should collect Composite Id value as Map"(){
        given:
        EntityType entity = typeFactory.create(new EntityDefinition(Person))
        def person = new Person(name: "mad", surname: "kaz", dob: LocalDate.of(2019,01,01), data: 1)

        expect:
        entity.getIdOf(person) == [name: "mad", surname: "kaz", dob: LocalDate.of(2019,01,01)]
    }

    def "should not put nulls to Composite Id Map"(){
        given:
        EntityType entity = typeFactory.create(new EntityDefinition(Person))
        def person = new Person(name: "mad", surname: "kaz")

        expect:
        entity.getIdOf(person) == [name: "mad", surname: "kaz"]
        entity.getIdOf(person).size() == 2
    }

    def "should throw ENTITY_INSTANCE_WITH_NULL_COMPOSITE_ID when all Id-properties are null "(){
        given:
        EntityType entity = typeFactory.create(new EntityDefinition(Person))
        def person = new Person(name:null)

        when:
        entity.getIdOf(person)

        then:
        JaversException e = thrown()
        e.code == ENTITY_INSTANCE_WITH_NULL_COMPOSITE_ID
    }

    def "should create proper InstanceId for an Entity with Composite Id"(){
        given:
        EntityType entity = typeFactory.create(new EntityDefinition(Person))
        def person = new Person(name: "mad", surname: "kaz", dob: LocalDate.of(2019,01,01), data: 1)

        when:
        def instanceId = entity.createIdFromInstance(person)

        then:
        instanceId.value().endsWith("Person/2019,1,1,mad,kaz")
        instanceId.cdoId == "2019,1,1,mad,kaz"
    }
}
