 package org.javers.common.reflection
import com.google.common.reflect.TypeToken
import org.javers.core.model.DummyUser
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass
/**
 * @author Pawel Cierpiatka
 */
class ReflectionUtilTest extends Specification {
    class ReflectionTestModel {
        List<DummyUser> dummyUserList
        Set noGenericSet
    }

    def "should instantiate via public constructor with ArgumentsResolver"() {
        given:
        ArgumentResolver argumentResolver = Mock()
        argumentResolver.resolve(_) >> "zonk"

        when:
        def instance = ReflectionUtil.newInstance(ReflectionConstructorTestClass, argumentResolver)

        then:
        instance instanceof ReflectionConstructorTestClass
        instance.someString == "zonk"
    }

    def "should instantiate via public zero.arg constructor"() {
        when:
        def instance = ReflectionUtil.newInstance(ReflectionTestClass, null)

        then:
        instance instanceof ReflectionTestClass
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

    def "should return actual type argument from field"() {
        given:
        def dummyUsersList = getFieldFromClass(ReflectionTestModel.class, "dummyUserList")

        when:
        def args = ReflectionUtil.extractActualClassTypeArguments(dummyUsersList.genericType)

        then:
        args[0] == DummyUser
    }

    @Unroll
    def "should replace formal type parameter with actual type argument for inherited #memberType"() {
        when:
        def member = action.call()

        then:
        member.genericType == new TypeToken<List<String>>(){}.type

        println member

        where:
        memberType | action
        "Method"   | { ReflectionUtil.getAllMethods(ConcreteWithActualType)[0] }
        "Field"    | { ReflectionUtil.getAllFields(ConcreteWithActualType)[0] }
    }

    def "should return empty list when type is not generic"() {
        given:
        def noGenericSet = getFieldFromClass(ReflectionTestModel.class, "noGenericSet")

        when:
        def args = ReflectionUtil.extractActualClassTypeArguments(noGenericSet.genericType)

        then:
        args == []
    }
}