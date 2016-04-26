package org.javers.common.reflection

import com.google.common.reflect.TypeToken
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.model.DummyIgnoredType
import org.javers.core.model.IgnoredSubType
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Pawel Cierpiatka
 */
class ReflectionUtilTest extends Specification {

    def "should detect annotations in a given class and it superclass"(){
        expect:
        ReflectionUtil.isAnnotationPresentInHierarchy(DummyIgnoredType, DiffIgnore)
        ReflectionUtil.isAnnotationPresentInHierarchy(IgnoredSubType, DiffIgnore)
        !ReflectionUtil.isAnnotationPresentInHierarchy(ArrayList, DiffIgnore)
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

    @Unroll
    def "should replace formal type parameter with actual type argument for inherited #memberType"() {
        when:
        def member = action.call()

        then:
        member.genericResolvedType == new TypeToken<List<String>>(){}.type

        println member

        where:
        memberType | action
        "Method"   | { ReflectionUtil.getAllMethods(ConcreteWithActualType).find{it.name() == "getValue"} }
        "Field"    | { ReflectionUtil.getAllFields(ConcreteWithActualType).find{it.name() == "value"} }
    }
}