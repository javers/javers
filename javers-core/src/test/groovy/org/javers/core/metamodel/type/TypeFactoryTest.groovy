package org.javers.core.metamodel.type

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.MappingStyle
import org.javers.core.examples.typeNames.*
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.clazz.JaversEntity
import org.javers.core.metamodel.clazz.JaversValue
import org.javers.core.metamodel.clazz.ValueObjectDefinition
import org.javers.core.metamodel.scanner.ClassScanner
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyIgnoredType
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.metamodel.clazz.EntityDefinitionBuilder.entityDefinition
import static org.javers.core.metamodel.clazz.ValueObjectDefinitionBuilder.valueObjectDefinition

/**
 * @author Pawel Cierpiatka
 */
class TypeFactoryTest extends Specification {

    static def entityCreator(MappingStyle mappingStyle){
        def typeFactory = create(mappingStyle)
        return { clazz -> typeFactory.createEntity(clazz)}
    }

    static TypeFactory create(MappingStyle mappingStyle){
        def javersTestAssembly = javersTestAssembly(mappingStyle)
        def classScanner = javersTestAssembly.getContainerComponent(ClassScanner)
        new TypeFactory(classScanner, javersTestAssembly.typeMapper)
    }

    def setupSpec() {
        typeFactory = create(MappingStyle.FIELD)
    }

    @Shared
    def TypeFactory typeFactory

    def "should use name from @TypeName when inferring from prototype"(){
        given:
        def prototype = typeFactory.inferFromAnnotations(AbstractValueObject)

        when:
        def jType = typeFactory.infer(NewNamedValueObject, org.javers.common.collections.Optional.of(prototype))

        then:
        jType.name == OldValueObject.name
    }

    @Unroll
    def "should use name from @TypeName for inferred #expectedType.simpleName"(){
        when:
        def type = typeFactory.inferFromAnnotations(clazz)

        then:
        type.name == "myName"
        type.class == expectedType

        where:
        expectedType  << [ValueObjectType, EntityType]
        clazz << [JaversValueObjectWithTypeAlias, NewEntityWithTypeAlias]
    }

    @Unroll
    def "should use typeName from ClientClassDefinition for #expectedType.simpleName"(){
        when:
        def type = typeFactory.create(definition)

        then:
        type.name == "myName"
        type.class == expectedType

        where:
        expectedType  << [EntityType,ValueObjectType]
        definition << [entityDefinition(DummyUser).withTypeName("myName").build(),
                       valueObjectDefinition(DummyUser).withTypeName("myName").build()
        ]
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

    def "should ignore properties with @DiffIgnored type"(){
        when:
        EntityType entity = typeFactory.inferFromAnnotations(DummyUser)

        then:
        !entity.propertyNames.contains("propertyWithDiffIgnoredType")
        !entity.propertyNames.contains("propertyWithDiffIgnoredSubtype")
    }

    def "should map @DiffIgnored type as IgnoredType"(){
        expect:
        typeFactory.inferFromAnnotations(DummyIgnoredType) instanceof IgnoredType
    }

    def "should map as ValueObjectType by default"(){
        expect:
        typeFactory.inferFromAnnotations(DummyAddress) instanceof ValueObjectType
    }

    def "should map as ValueType when @Value annotation is present "(){
        expect:
        typeFactory.inferFromAnnotations(JaversValue) instanceof ValueType
    }

    def "should map as EntityType if property level @Id annotation is present"() {
        expect:
        typeFactory.inferFromAnnotations(DummyUser) instanceof EntityType
    }

    def "should map as EntityType when @Entity annotation is present"() {
        expect:
        typeFactory.inferFromAnnotations(JaversEntity) instanceof EntityType
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