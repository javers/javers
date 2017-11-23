package org.javers.common.reflection

import com.google.common.reflect.TypeToken
import org.javers.core.examples.typeNames.NewEntityWithTypeAlias
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.clazz.JaversEntity
import org.javers.core.metamodel.clazz.JpaMappedSuperclass
import org.javers.core.model.DummyIgnoredType
import org.javers.core.model.IgnoredSubType
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.MappedSuperclass

/**
 * @author Pawel Cierpiatka
 */
class ReflectionUtilTest extends Specification {

    def "should scan classes in a given package (with a given annotation)"(){
        given:
        def scan = ReflectionUtil.findClasses(MappedSuperclass, 'org.javers.core.metamodel.clazz')

        expect:
        scan.size() == 1
        scan[0] == JpaMappedSuperclass
    }

    def "should scan classes in a given packages (with a given annotation)"(){
        given:
        def scan = ReflectionUtil.findClasses(Entity, 'org.javers.core.metamodel.clazz', 'org.javers.core.examples.typeNames')

        expect:
        scan.contains(JaversEntity)
        scan.contains(NewEntityWithTypeAlias)
    }

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

    def "should calculate hierarchy distance as follows (parents first, interfaces last)"() {
        expect:
        ReflectionUtil.calculateHierarchyDistance(HashMap) == [AbstractMap, Map, Cloneable, Serializable]
    }

    def "should get all methods from a given class without inheritance duplicates"(){
        when:
        def methods = ReflectionUtil.getAllGetters(ReflectionTestClass)

        methods.each { println it }

        then:
        methods.size() == 5
    }

    @Unroll
    def "should resolve type variable with actual type argument for inherited #memberType"() {
        when:
        def member = action.call()

        then:
        member.genericResolvedType == new TypeToken<List<String>>(){}.type

        where:
        memberType | action
        "Method"   | { ReflectionUtil.getAllGetters(ConcreteWithActualType).find{it.name() == "getValue"} }
        "Field"    | { ReflectionUtil.getAllFields(ConcreteWithActualType).find{it.name() == "value"} }
    }

    @Unroll
    def "should resolve type variable for inherited #memberType when inheritance hierarchy has three levels"() {
        when:
        def member = action.call()

        then:
        member.genericResolvedType == Long

        where:
        memberType | action
        "Method"   | { ReflectionUtil.getAllGetters(ConcreteIdentified).find{it.name() == "getId"} }
        "Field"    | { ReflectionUtil.getAllFields(ConcreteIdentified).find{it.name() == "id"} }
    }
}