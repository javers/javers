package org.javers.core.metamodel.type;

import com.google.gson.reflect.TypeToken
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

    def "should map as ValueObject by default"() {
        given:
        TypeMapper mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)

        when:
        def jType = mapper.getJaversType(DummyAddress)

        then:
        jType.class == ValueObjectType
        jType.baseJavaClass == DummyAddress
    }

    def "should map as Entity when class has id property"() {
        given:
        TypeMapper mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)

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
