package org.javers.core.metamodel.type;

import com.google.gson.reflect.TypeToken
import org.javers.core.metamodel.property.Entity
import org.javers.core.metamodel.property.EntityDefinition;
import org.javers.core.metamodel.property.ValueObjectDefinition;
import org.javers.core.model.AbstractDummyUser
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser;
import spock.lang.Specification;
import spock.lang.Unroll;

import java.util.Calendar;

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

    def "should spawn EntityType from mapped superclass"() {
        given:
        TypeMapper mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
        mapper.registerManagedClass(new EntityDefinition(AbstractDummyUser,"inheritedInt"))

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == EntityType
        jType.baseJavaClass == DummyUser
    }

    class DummyGenericUser<T> extends AbstractDummyUser {}

    @Unroll
    def "should spawn #expectedJaversType.simpleName for #queryTypeAsString from the nearest prototype"() {
        given:
        TypeMapper mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
        mapper.registerValueType(Object.class)
        mapper.registerManagedClass(new ValueObjectDefinition(AbstractDummyUser))

        when:
        def jType = mapper.getJaversType(queryType)

        then:
        jType.class == expectedJaversType

        where:
        queryType        | queryTypeAsString  || expectedJaversType
        Calendar         | "Calendar"         || ValueType
        DummyGenericUser | "DummyGenericUser" || ValueObjectType
        new TypeToken<DummyGenericUser<String>>(){}.type | "DummyGenericUser<String>" || ValueObjectType
    }

    def "should spawn ValueObjectType from mapped superclass"() {
        given:
        TypeMapper mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
        mapper.registerManagedClass(new ValueObjectDefinition(AbstractDummyUser))

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == ValueObjectType
        jType.baseJavaClass == DummyUser
    }
}
