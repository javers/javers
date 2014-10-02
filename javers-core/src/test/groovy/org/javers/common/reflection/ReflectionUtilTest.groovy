 package org.javers.common.reflection

import org.javers.core.model.DummyUser
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Field
import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author Pawel Cierpiatka
 */
class ReflectionUtilTest extends Specification {
    class ReflectionTestModel {
        List<DummyUser> dummyUserList
        Set noGenericSet
    }

    @Unroll
    def "should calculate hierarchy distance from #child to #parent"() {
        when:
            int d = ReflectionUtil.calculateHierarchyDistance(child, parent)

        then:
            d == expectedDistance

        where:
        child   | parent      || expectedDistance
        HashMap | Map         || 1
        HashMap | HashMap     || 0
        Map     | Map         || 0
        HashMap | AbstractMap || 1
        HashMap | Object      || 2
        Map     | Set         || Integer.MAX_VALUE

    }

    def "should return actual class type argument from field"() {
        given :
            Field dummyUsersList = getFieldFromClass(ReflectionTestModel.class, "dummyUserList")

        when :
            Class[] args = ReflectionUtil.extractActualClassTypeArguments(dummyUsersList.genericType)

        then:
            args[0] == DummyUser
    }

    def "should return empty list when type is not generic"() {
        given :
            Field noGenericSet = getFieldFromClass(ReflectionTestModel.class, "noGenericSet")

        when :
            Class[] args = ReflectionUtil.extractActualClassTypeArguments(noGenericSet.genericType)

        then:
            args == []
    }

    def "should return distinct method keys"() {
        given:

        when:
        def aKey = ReflectionUtil.methodKey(ReflectionTestClass.class.getMethod("Aa", String.class))
        def bKey = ReflectionUtil.methodKey(ReflectionTestClass.class.getMethod("BB", String.class))

        println("aKey $aKey")
        println("bKey $bKey")

        then:
        aKey != bKey
    }
}