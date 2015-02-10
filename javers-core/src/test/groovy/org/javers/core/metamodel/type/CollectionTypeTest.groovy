package org.javers.core.metamodel.type

import com.google.common.reflect.TypeToken
import org.javers.common.exception.JaversException
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.common.exception.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED
import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class CollectionTypeTest extends Specification{
    class Dummy <T> {
        Set rawType
        Set<?> unboundedWildcardType
        Set<T> genericType
        Set<String> parametrizedType
        Set<ThreadLocal<String>> nestedParametrizedType
    }

    @Unroll
    def "should fail for #fieldName"(){
        given:
        def noGeneric = getFieldFromClass(Dummy, fieldName).genericType

        when:
        def cType = new ListType(noGeneric).getItemClass()

        then:
        def e = thrown(JaversException)
        e.code == GENERIC_TYPE_NOT_PARAMETRIZED
        println e.message

        where:
        fieldName    << ["rawType", "unboundedWildcardType", "genericType"]
    }

    def "should scan actual class from type parameter" () {
        given:
        def genericWithArgument = getFieldFromClass(Dummy, "parametrizedType").genericType

        when:
        def cType = new ListType(genericWithArgument)

        then:
        cType.baseJavaType == new TypeToken<Set<String>>(){}.type
        cType.genericType == true
        cType.fullyParametrized == true
        cType.itemType == String
    }

    def "should scan nested generic type from type parameter" () {
        given:
        def genericWithArgument = getFieldFromClass(Dummy, "nestedParametrizedType").genericType

        when:
        def cType = new ListType(genericWithArgument)

        then:
        cType.baseJavaType == new TypeToken< Set<ThreadLocal<String>> >(){}.type
        cType.fullyParametrized == true
        cType.itemType ==  new TypeToken< ThreadLocal<String> >(){}.type
    }
}
