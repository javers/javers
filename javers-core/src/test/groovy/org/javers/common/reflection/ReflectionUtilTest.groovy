package org.javers.common.reflection

import org.javers.core.model.DummyUser
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Method

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass
import static org.javers.common.reflection.ReflectionTestHelper.getMethodFromClass
import static org.javers.common.reflection.ReflectionUtil.getGenericTypeClass

/**
 * @author Pawel Cierpiatka
 */
class ReflectionUtilTest extends Specification {

    def "should return declared list type class from field"() {

        given :
            Field dummyUsersList = getFieldFromClass(ReflectionTestModel.class, "dummyUserList");

        when :
            Class arrayDeclaredType = getGenericTypeClass(dummyUsersList.getGenericType());

        then:
            arrayDeclaredType == DummyUser.class

    }

    def "should return declared set type class from field"() {

        given :
            Field dummyUsersSet = getFieldFromClass(ReflectionTestModel.class, "dummyUserSet");

        when :
            Class setDeclaredType = getGenericTypeClass(dummyUsersSet.getGenericType());

        then :
            setDeclaredType == DummyUser.class
    }

    def "should return declared queue type class from field"() {
        given  :
            Field dummyUsersQueue = getFieldFromClass(ReflectionTestModel.class, "dummyUserQueue");

        when :
            Class queueDeclaredType = getGenericTypeClass(dummyUsersQueue.getGenericType());

        then :
            queueDeclaredType == DummyUser.class;
    }

    def "should return declared list type class from method"() {
        given :
            Method dummyUsersList = getMethodFromClass(ReflectionTestModel.class, "getDummyUserList");

        when :
            Class arrayDeclaredType = getGenericTypeClass(dummyUsersList.getGenericReturnType());

        then :
            arrayDeclaredType == DummyUser.class
    }

    def "should return declared set type class from method"() {
        given :
            Method dummyUsersSet = getMethodFromClass(ReflectionTestModel.class, "getDummyUserSet");

        when :
            Class setDeclaredType = getGenericTypeClass(dummyUsersSet.getGenericReturnType());

        then :
            setDeclaredType == DummyUser.class
    }

    def "should return declared queue type class from method"() {
        given:
            Method dummyUsersList = getMethodFromClass(ReflectionTestModel.class, "getDummyUserList");

        when :
            Class arrayDeclaredType = getGenericTypeClass(dummyUsersList.getGenericReturnType());

        then :
            arrayDeclaredType == DummyUser.class
    }
}