package org.javers.core.metamodel.type

import org.javers.core.metamodel.type.ArrayType
import spock.lang.Specification

import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class ArrayTypeTest extends Specification{
    class Dummy <T> {
        Integer[] intArray
    }

    def "should hold actual elementType" () {
        given:
        Type intArray   =    getFieldFromClass(Dummy, "intArray").genericType

        when:
        ArrayType aType = new ArrayType(intArray)

        then:
        aType.baseJavaType == Integer[]
        aType.genericType == false
        aType.elementType == Integer
    }
}
