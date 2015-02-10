package org.javers.core.metamodel.type

import com.google.gson.reflect.TypeToken
import org.javers.common.exception.JaversException
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.common.exception.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED
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
    def "should not be fully parametrized if baseJavaType is not #geneticKind"(){
        given:
        Type noGeneric = getFieldFromClass(Dummy, geneticKind).genericType

        when:
        MapType mType = new MapType(noGeneric)

        then:
        mType.fullyParametrized == false

        when:
        mType.getKeyType()

        then:
        def e = thrown(JaversException)
        e.code == GENERIC_TYPE_NOT_PARAMETRIZED;

        when:
        mType.getValueType()

        then:
        e = thrown(JaversException)
        e.code == GENERIC_TYPE_NOT_PARAMETRIZED;

        where:
        geneticKind << ["noGeneric","wildcardGeneric","parametrizedGeneric"]
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
}
