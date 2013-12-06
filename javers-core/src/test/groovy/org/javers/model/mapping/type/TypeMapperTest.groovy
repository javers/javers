package org.javers.model.mapping.type

import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class TypeMapperTest extends Specification {

    class Dummy <T,X> {
        Set    noGenericGeneric
        Set<?> wildcardGeneric
        Set<T> parametrizedGeneric
        Set<X> parametrizedGenericX
        Set<String> genericWithString
    }

    def "should map parametrized types as distinct javers types"() {
        given:
        TypeMapper mapper = new TypeMapper();
        Type parametrizedGenericType   = getFieldFromClass(Dummy, "parametrizedGeneric").genericType
        Type parametrizedGenericTypeX  = getFieldFromClass(Dummy, "parametrizedGenericX").genericType
        Type genericWithStringType =     getFieldFromClass(Dummy, "genericWithString").genericType

        when:
        mapper.registerCollectionType(parametrizedGenericType)
        mapper.registerCollectionType(parametrizedGenericTypeX)
        mapper.registerCollectionType(genericWithStringType)
        mapper.getMappedTypes(CollectionType).each {System.out.println(it)}

        then:
        mapper.getMappedTypes(CollectionType).size() == 2

    }

}
