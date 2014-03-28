package org.javers.core.metamodel.type

import com.google.common.reflect.TypeToken
import spock.lang.Specification

import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class CollectionTypeTest extends Specification{
    class Dummy <T> {
        Set noGeneric
        Set<?> wildcardGeneric
        Set<T> parametrizedGeneric
        Set<String> genericWithArgument
    }

    def "should not be generic if baseJavaType is not generic"(){
        given:
        Type noGeneric = getFieldFromClass(Dummy, "noGeneric").genericType

        when:
        CollectionType cType = new CollectionType(noGeneric)

        then:
        cType.baseJavaType == Set
        cType.genericType == false
        cType.elementTypes == null
    }

    def "should ignore unbounded type parameter" () {
        given:
        Type parametrizedGeneric = getFieldFromClass(Dummy, "parametrizedGeneric").genericType

        when:
        CollectionType cType = new CollectionType(parametrizedGeneric)

        then:
        cType.genericType == true
        cType.elementTypes == null
    }

    def "should hold actual elementType" () {
        given:
        Type genericWithArgument   =    getFieldFromClass(Dummy, "genericWithArgument").genericType

        when:
        CollectionType cType = new CollectionType(genericWithArgument)

        then:
        cType.baseJavaType == new TypeToken<Set<String>>(){}.type
        cType.genericType == true
        cType.elementTypes == String
    }
}
