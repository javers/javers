package org.javers.core.metamodel.type;

import com.google.gson.reflect.TypeToken
import org.bson.types.ObjectId
import org.javers.core.cases.morphia.MongoStoredEntity
import org.javers.core.metamodel.clazz.*
import org.javers.core.model.AbstractDummyUser
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser;
import spock.lang.Specification;
import spock.lang.Unroll

import static org.javers.core.JaversTestBuilder.javersTestAssembly;

/**
 * @author bartosz walacik
 */
public class TypeMapperIntegrationTest extends Specification {

    @Unroll
    def "should override Entity type inferred form annotations when ValueObject is explicitly registered for #queryClass.simpleName"() {
        given:
        def mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
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
        def mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
        mapper.registerClientsClass(new EntityDefinition(JpaEmbeddable,"some"))

        when:
        EntityType jType = mapper.getJaversType(JpaEmbeddable)

        then:
        jType.managedClass.idProperty.name == "some"
    }

    def "should map as ValueObject by default"() {
        given:
        def mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)

        when:
        def jType = mapper.getJaversType(DummyAddress)

        then:
        jType.class == ValueObjectType
        jType.baseJavaClass == DummyAddress
    }

    def "should map as Value if the class is used as @Id in another class"(){
        given:
        def mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)

        when:
        entityRegisterAction.call(mapper)
        def jType = mapper.getJaversType(ObjectId)

        then:
        jType.class == ValueType
        jType.baseJavaClass == ObjectId

        where:
        entityRegisterAction << [
            { m -> m.registerClientsClass(new EntityDefinition(MongoStoredEntity)) },
            { m -> m.getJaversType(MongoStoredEntity) }
        ]
    }

    def "should map as Entity when class has @Id property annotation"() {
        given:
        def mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == EntityType
        jType.baseJavaClass == DummyUser
    }

    @Unroll
    def "should map as #expectedJaversType.simpleName for annotated class #queryClass.simpleName"() {
        given:
        def mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)

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
        def mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
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
        def mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
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

    def "should spawn from mapped superclass when querying for generic class"() {
        given:
        def mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
        mapper.registerClientsClass(new ValueDefinition(AbstractDummyUser))

        when:
        def jType = mapper.getJaversType(new TypeToken<DummyGenericUser<String>>(){}.type)

        then:
        jType.class == ValueType
        jType.baseJavaClass == DummyGenericUser
    }
    class DummyGenericUser<T> extends AbstractDummyUser {}
}
