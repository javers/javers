package org.javers.core.metamodel.type

import com.google.common.reflect.TypeToken
import org.javers.common.exception.exceptions.JaversException
import spock.lang.Specification

import java.lang.reflect.Type

import static org.javers.common.exception.exceptions.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED
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
        cType.fullyParameterized == false

        when:
        cType.getItemClass()

        then:
        def e = thrown(JaversException)
        e.code == GENERIC_TYPE_NOT_PARAMETRIZED;
    }

    def "should ignore unbounded type parameter" () {
        given:
        Type parametrizedGeneric = getFieldFromClass(Dummy, "parametrizedGeneric").genericType

        when:
        CollectionType cType = new CollectionType(parametrizedGeneric)

        then:
        cType.genericType == true
        cType.fullyParameterized == false
    }

    def "should hold actual elementType" () {
        given:
        Type genericWithArgument   =    getFieldFromClass(Dummy, "genericWithArgument").genericType

        when:
        CollectionType cType = new CollectionType(genericWithArgument)

        then:
        cType.baseJavaType == new TypeToken<Set<String>>(){}.type
        cType.genericType == true
        cType.fullyParameterized == true
        cType.itemClass == String
    }
}
