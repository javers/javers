package org.javers.core.metamodel.type

import com.google.common.reflect.TypeToken
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class CollectionTypeTest extends Specification{

    class Dummy <T> {
        Set rawType
        Set<?> unboundedWildcardType
        Set<? extends String> boundedWildcardType
        Set<T> genericType
        Set<String> parametrizedType
        Set<ThreadLocal<String>> nestedParametrizedType
    }

    @Unroll
    def "should default non-concrete type arguments to Object for #fieldName"(){
        given:
        def noGeneric = getFieldFromClass(Dummy, fieldName).genericType

        when:
        def itemType = listType(noGeneric).getItemClass()

        then:
        itemType == Object

        where:
        fieldName << ["rawType", "unboundedWildcardType", "genericType"]
    }

    def "should scan actual class from type parameter" () {
        given:
        def genericWithArgument = getFieldFromClass(Dummy, "parametrizedType").genericType

        when:
        def cType = listType(genericWithArgument)

        then:
        cType.baseJavaType == new TypeToken<Set<String>>(){}.type
        cType.genericType == true
        cType.itemJavaType == String
    }

    def "should treat wildcards with an upper bound as the type of its upper bound" () {
        given:
        def genericWithArgument = getFieldFromClass(Dummy, "boundedWildcardType").genericType

        when:
        def cType = listType(genericWithArgument)

        then:
        cType.baseJavaType == new TypeToken<Set<? extends String>>(){}.type
        cType.genericType == true
        cType.itemJavaType == String
    }

    def "should scan nested generic type from Set type parameter" () {
        given:
        def genericWithArgument = getFieldFromClass(Dummy, "nestedParametrizedType").genericType

        when:
        def cType = setType(genericWithArgument)

        then:
        cType.baseJavaType == new TypeToken< Set<ThreadLocal<String>> >(){}.type
        cType.itemJavaType ==  new TypeToken< ThreadLocal<String> >(){}.type
    }

    ListType listType(Type type) {
        new ListType(type, { it -> new ValueType(Object) })
    }

    SetType setType(Type type) {
        new SetType(type, { it -> new ValueType(Object) })
    }
}
