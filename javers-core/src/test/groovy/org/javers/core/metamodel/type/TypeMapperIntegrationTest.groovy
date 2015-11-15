package org.javers.core.metamodel.type

import com.google.gson.reflect.TypeToken
import org.bson.types.ObjectId
import org.javers.core.cases.MongoStoredEntity
import org.javers.core.examples.typeNames.JaversEntityWithTypeAlias
import org.javers.core.metamodel.clazz.*
import org.javers.core.model.*
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.EmbeddedId
import javax.persistence.Id

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.metamodel.clazz.EntityDefinition.EntityDefinitionBuilder.entityDefinition

/**
 * @author bartosz walacik
 */
public class TypeMapperIntegrationTest extends Specification {

    def "should find EntityType by typeName when class has @TypeName annotation and its package is registered"(){
        given:
        def mapper = javersTestAssembly("org.javers.core.metamodel.clazz").typeMapper

        when:
        def type = mapper.getEntityByName("myName")

        then:
        type.baseJavaClass == JaversEntityWithTypeAlias
    }

    def "should find EntityType by typeName when class is registered using EntityDefinition"(){
        given:
        def mapper = javersTestAssembly().typeMapper

        when:
        mapper.registerClientsClass(entityDefinition(DummyUser).withTypeName("DummyType").build())
        def type = mapper.getEntityByName("DummyType")

        then:
        type.baseJavaClass == DummyUser
    }

    @Unroll
    def "should override Entity type inferred form annotations when ValueObject is explicitly registered for #queryClass.simpleName"() {
        given:
        def mapper = javersTestAssembly().typeMapper
        mapper.registerClientsClass(new ValueObjectDefinition(queryClass))

        when:
        def jType = mapper.getJaversType(queryClass)

        then:
        jType.class == ValueObjectType
        jType.baseJavaClass == queryClass

        where:
        queryClass <<  [JpaEntity,
                        ClassWithEntityAnn,
                        ClassWithIdAnn]
    }

    def "should override ValueObject type inferred form annotations when Entity is explicitly registered"() {
        given:
        def mapper = javersTestAssembly().typeMapper
        mapper.registerClientsClass(new EntityDefinition(JpaEmbeddable,"some"))

        when:
        def jType = mapper.getJaversType(JpaEmbeddable)

        then:
        jType.class == EntityType
        jType.idProperty.name == "some"
    }

    def "should map as ValueObject by default"() {
        given:
        def mapper = javersTestAssembly().typeMapper

        when:
        def jType = mapper.getJaversType(DummyAddress)

        then:
        jType.class == ValueObjectType
        jType.baseJavaClass == DummyAddress
    }

    @Unroll
    def "should map as ValueType if a class is used as #usedAnn.simpleName in another class"(){
        given:
        def mapper = javersTestAssembly().typeMapper

        when:
        mapper.getJaversType(entity)
        def jType = mapper.getJaversType(idType)

        then:
        jType.class == ValueType
        jType.baseJavaClass == idType

        where:
        entity <<  [MongoStoredEntity, DummyEntityWithEmbeddedId]
        usedAnn << [Id, EmbeddedId]
        idType <<  [ObjectId, DummyPoint]
    }

    def "should map as Entity when class has @Id property annotation"() {
        given:
        def mapper = javersTestAssembly().typeMapper

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == EntityType
        jType.baseJavaClass == DummyUser
    }

    @Unroll
    def "should map as #expectedJaversType.simpleName for annotated class #queryClass.simpleName"() {
        given:
        def mapper = javersTestAssembly().typeMapper

        when:
        def jType = mapper.getJaversType(queryClass)

        then:
        jType.class == expectedJaversType
        jType.baseJavaClass == queryClass

        where:
        queryClass << [JaversEntity,
                       JaversValueObject,
                       JaversValue,
                       JpaEntity,
                       JpaEmbeddable]
        expectedJaversType << [EntityType, ValueObjectType, ValueType, EntityType, ValueObjectType]

    }

    @Unroll
    def "should map as #expectedJaversType.simpleName for explicitly registered class"() {
        given:
        def mapper = javersTestAssembly().typeMapper
        mapper.registerClientsClass(givenDefinition)

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == expectedJaversType
        jType.baseJavaClass == DummyUser

        where:
        givenDefinition << [
                new EntityDefinition(DummyUser,"inheritedInt"),
                new ValueObjectDefinition(DummyUser),
                new ValueDefinition(DummyUser)]
        expectedJaversType << [EntityType, ValueObjectType, ValueType]
    }

    @Unroll
    def "should spawn #expectedJaversType.simpleName from mapped superclass"() {
        given:
        def mapper = javersTestAssembly().typeMapper
        mapper.registerClientsClass(givenDefinitionOfSuperclass)

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == expectedJaversType
        jType.baseJavaClass == DummyUser

        where:
        givenDefinitionOfSuperclass << [
                new EntityDefinition(AbstractDummyUser,"inheritedInt"),
                new ValueObjectDefinition(AbstractDummyUser),
                new ValueDefinition(AbstractDummyUser)]
        expectedJaversType << [EntityType, ValueObjectType, ValueType]
    }

    def "should inherit custom idProperty mapping from explicitly mapped Entity"() {
        given:
        def mapper = javersTestAssembly().typeMapper

        when:
        mapper.registerClientsClass(new EntityDefinition(AbstractDummyUser,"inheritedInt"))
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType instanceof EntityType
        jType.idProperty.name == "inheritedInt"
    }

    def "should spawn from mapped superclass when querying for generic class"() {
        given:
        def mapper = javersTestAssembly().typeMapper
        mapper.registerClientsClass(new ValueDefinition(AbstractDummyUser))

        when:
        def jType = mapper.getJaversType(new TypeToken<DummyGenericUser<String>>(){}.type)

        then:
        jType.class == ValueType
        jType.baseJavaClass == DummyGenericUser
    }
    class DummyGenericUser<T> extends AbstractDummyUser {}
}
