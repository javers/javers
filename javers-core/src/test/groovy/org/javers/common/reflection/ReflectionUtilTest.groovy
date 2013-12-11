package org.javers.common.reflection

import org.javers.core.model.DummyUser
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Method

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass
import static org.javers.common.reflection.ReflectionTestHelper.getMethodFromClass
import static org.javers.common.reflection.ReflectionUtil.getParametrizedTypeFirstArgument

/**
 * @author Pawel Cierpiatka
 */
class ReflectionUtilTest extends Specification {
    class ReflectionTestModel {
        List<DummyUser> dummyUserList
        Set noGenericSet
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


}