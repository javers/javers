package org.javers.core.metamodel.type

import org.javers.core.model.GenericArrayContainerInJava
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class ArrayTypeTest extends Specification{
    class Dummy <T> {
        Integer[] intArray
    }

    class GenericArrayContainerInGroovy<T> {
        T[] array
    }


    def "should hold actual elementType" () {
        given:
        Type intArray   =    getFieldFromClass(Dummy, "intArray").genericType

        when:
        ArrayType aType = arrayType(intArray)

        then:
        aType.baseJavaType == Integer[]
        aType.genericType == false
        aType.itemClass == Integer
    }

    @Unroll
    def "should default non-concrete array elementType to Object when domain class is written in #lang"() {
        given:
        def nonConcreteArray = getFieldFromClass(domainClass, "array").genericType
        when:
        def itemType = arrayType(nonConcreteArray).getItemClass()

        then:
        itemType == Object

        where:
        lang << ["java", "groovy"]
        domainClass << [GenericArrayContainerInJava, GenericArrayContainerInGroovy]
    }

    ArrayType arrayType(Type type) {
        new ArrayType(type, { it -> new ValueType(Object) })
    }
}
