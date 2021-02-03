package org.javers.core.metamodel.type

import com.google.gson.reflect.TypeToken
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class MapTypeTest extends Specification{

    enum DummyEnum{}
    
    class Dummy <T> {
        Map                  noGeneric
        Map<?, ?>       wildcardGeneric
        Map<T, T>       parametrizedGeneric
        Map<String, Integer> genericWithArgument
        Map<String, EnumSet<DummyEnum>> mapWithNestedParametrizedType
    }

    @Unroll
    def "should replace non-concrete type parameters with Object for type: #genericKind"(){
        given:
        def genericType = getFieldFromClass(Dummy, genericKind).genericType

        when:
        def mType = new MapType(genericType)

        then:
        mType.getKeyType() == Object
        mType.getValueType() == Object

        where:
        genericKind << ["noGeneric","wildcardGeneric","parametrizedGeneric"]
    }

    def "should return key & value Class if baseJavaType is generic with actual Class argument"(){
        given:
        Type noGeneric = getFieldFromClass(Dummy, "genericWithArgument").genericType

        when:
        MapType mType = new MapType(noGeneric)

        then:
        mType.baseJavaType == new TypeToken<Map<String,Integer>>(){}.type
        mType.keyType == String
        mType.valueType == Integer
    }

    def "should scan nested generic type from Map value type parameter" () {
        given:
        def genericWithGenericArgument = getFieldFromClass(Dummy, "mapWithNestedParametrizedType").genericType

        when:
        def mType = new MapType(genericWithGenericArgument)

        then:
        mType.baseJavaType == new TypeToken<Map<String,EnumSet<DummyEnum>>>(){}.type
        mType.keyType == String
        mType.valueType == new TypeToken< EnumSet<DummyEnum> >(){}.type
    }
}
