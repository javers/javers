package org.javers.core.metamodel.clazz

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.MappingStyle
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author Pawel Cierpiatka
 */
class ManagedClassFactoryTest extends Specification {

    def setupSpec() {
        managedClassFactory = javersTestAssembly(MappingStyle.BEAN).managedClassFactory
    }

    @Shared
    def ManagedClassFactory managedClassFactory

    @Unroll
    @Ignore //TODO
    def "should use name from @TypeAlias for #what"(){
        when:
        def entity = managedClassFactory.inferFromAnnotations(clazz)

        then:
        entity.name == "NyName"

        where:
        what  << ["Entity"]
        clazz << [JaversEntityWithTypeAlias]
    }

    def "should create Entity with properties, ID property and reference to client's class"() {
        when:
        def entity = managedClassFactory.createEntity(DummyUser)

        then:
        entity instanceof Entity
        entity.getClientsClass() == DummyUser
        entity.properties.size() > 2
        entity.idProperty.name == "name"
    }

    def "should create ValueObject with properties and reference to client's class"() {
        when:
        def vo = managedClassFactory.createValueObject(DummyAddress)

        then:
        vo instanceof ValueObject
        vo.getClientsClass() == DummyAddress
        vo.properties.size() > 2
    }

    def "should map as ValueObject by default"(){
        when:
        def vo = managedClassFactory.inferFromAnnotations(DummyAddress)

        then:
        vo instanceof ValueObject
    }

    def "should map as Entity if property level @Id annotation is present"() {
        when:
        def vo = managedClassFactory.inferFromAnnotations(DummyUser)

        then:
        vo instanceof Entity
    }

    @Unroll
    def "should ignore given #managedClassType properties"() {
        when:
        def managedClass = managedClassFactory.create(managedClassRecipe)

        then:
        !managedClass.hasProperty("city")
        !managedClass.hasProperty("kind")

        where:
        managedClassType <<   ["Entity", "ValueObject"]
        managedClassRecipe << [ new EntityDefinition(DummyAddress, "street", ["city","kind"]),
                                new ValueObjectDefinition(DummyAddress, ["city","kind"]) ]
    }

    def "should fail if given ignored Entity property not exists"() {
        when:
        managedClassFactory.create(new EntityDefinition(DummyAddress, "street",["city__"]))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.PROPERTY_NOT_FOUND
    }

}