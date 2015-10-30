package org.javers.core.metamodel.type

import org.javers.common.collections.Optional
import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.MappingStyle
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.clazz.JaversEntityWithTypeAlias
import org.javers.core.metamodel.clazz.ValueObjectDefinition
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
class TypeFactoryTest extends Specification {

    def setupSpec() {
        typeFactory = javersTestAssembly(MappingStyle.BEAN).typeSpawningFactory
    }

    @Shared
    def TypeFactory typeFactory

    @Unroll
    @Ignore //TODO
    def "should use name from @TypeAlias for #what"(){
        when:
        def entity = typeFactory.infer(clazz, Optional.empty())

        then:
        entity.name == "NyName"

        where:
        what  << ["EntityType"]
        clazz << [JaversEntityWithTypeAlias]
    }

    def "should create EntityType with properties, ID property and reference to client's class"() {
        when:
        def entity = typeFactory.create(new EntityDefinition(DummyUser))

        then:
        entity instanceof EntityType
        entity.baseJavaClass == DummyUser
        entity.properties.size() > 2
        entity.idProperty.name == "name"
    }

    def "should create ValueObjectType with properties and reference to client's class"() {
        when:
        def vo = typeFactory.create(new ValueObjectDefinition(DummyAddress))


        then:
        vo instanceof ValueObjectType
        vo.baseJavaClass == DummyAddress
        vo.properties.size() > 2
    }

    def "should map as ValueObjectType by default"(){
        when:
        def vo = typeFactory.infer(DummyAddress, Optional.empty())

        then:
        vo instanceof ValueObjectType
    }

    def "should map as EntityType if property level @Id annotation is present"() {
        when:
        def vo = typeFactory.infer(DummyUser, Optional.empty())

        then:
        vo instanceof EntityType
    }

    @Unroll
    def "should ignore given #managedType properties"() {
        when:
        def jType = typeFactory.create(managedClassRecipe)

        then:
        !jType.hasProperty("city")
        !jType.hasProperty("kind")

        where:
        managedType <<   ["EntityType", "ValueObjectType"]
        managedClassRecipe << [ new EntityDefinition(DummyAddress, "street", ["city","kind"]),
                                new ValueObjectDefinition(DummyAddress, ["city","kind"]) ]
    }

    def "should fail if given ignored EntityType property not exists"() {
        when:
        typeFactory.create(new EntityDefinition(DummyAddress, "street",["city__"]))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.PROPERTY_NOT_FOUND
    }
}