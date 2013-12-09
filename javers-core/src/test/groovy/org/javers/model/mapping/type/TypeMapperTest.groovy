package org.javers.model.mapping.type

import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class TypeMapperTest extends Specification {

    enum DummyEnum {A,B}

    class Dummy <T,X> {
        HashSet hashSet
        HashSet<String> hashSetWithString
        DummyEnum dummyEnum

        Set<?> wildcardGeneric
        Set<T> parametrizedGeneric
        Set<X> parametrizedGenericX
        Set<String> genericWithString
    }

    def "should spawn concrete Enum type"() {
        given:
        TypeMapper mapper = new TypeMapper();
        Type dummyEnum   = getFieldFromClass(Dummy, "dummyEnum").type

        when:
        JaversType jType = mapper.getJavesrType(dummyEnum)

        then:
        jType.baseJavaType == DummyEnum
        jType.baseJavaClass == DummyEnum
    }


 //   def "should map parametrized types as distinct javers types"() {


}