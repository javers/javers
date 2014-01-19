package org.javers.model.mapping.type

import com.google.gson.reflect.TypeToken
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class MapTypeTest extends Specification{

    class Dummy <T> {
        Map                  noGeneric
        Map<?, String>       wildcardGeneric
        Map<String, T>       parametrizedGeneric
        Map<String, Integer> genericWithArgument
    }

    @Unroll
    def "should return null EntryClass if baseJavaType is not #geneticKind"(){
        given:
        Type noGeneric = getFieldFromClass(Dummy, geneticKind).genericType
        MapType mType = new MapType(noGeneric)

        expect:
        mType.entryClass == null

        where:
        geneticKind << ["noGeneric","wildcardGeneric","parametrizedGeneric"]
    }

    def "should return EntryClass if baseJavaType is generic with actual Class argument"(){
        given:
        Type noGeneric = getFieldFromClass(Dummy, "genericWithArgument").genericType

        when:
        MapType mType = new MapType(noGeneric)

        then:
        mType.baseJavaType == new TypeToken<Map<String,Integer>>(){}.type
        mType.entryClass.key == String
        mType.entryClass.value == Integer
    }
}
