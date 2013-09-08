package org.javers.common.reflection;

import org.javers.core.model.DummyUser;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass;
import static org.javers.common.reflection.ReflectionTestHelper.getMethodFromClass;
import static org.javers.common.reflection.ReflectionUtil.getGenericTypeClass;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
@Test
public class ReflectionUtilTest {

    public void shouldReturnDeclaredListTypeClassFromField(){
        //given
        Field dummyUsersList = getFieldFromClass(ReflectionTestModel.class, "dummyUserList");

        //when
        Class arrayDeclaredType = getGenericTypeClass(dummyUsersList.getGenericType());

        //then
        assertThat((arrayDeclaredType.isAssignableFrom(DummyUser.class))).isTrue();
    }

    public void shouldReturnDeclaredSetTypeClassFromField(){
        //given
        Field dummyUsersSet = getFieldFromClass(ReflectionTestModel.class, "dummyUserSet");

        //when
        Class setDeclaredType = getGenericTypeClass(dummyUsersSet.getGenericType());

        //then
        assertThat(setDeclaredType.isAssignableFrom(DummyUser.class)).isTrue();
    }

    public void shouldReturnDeclaredQueueTypeClassFromField(){
        //given
        Field dummyUsersQueue = getFieldFromClass(ReflectionTestModel.class, "dummyUserQueue");

        //when
        Class queueDeclaredType = getGenericTypeClass(dummyUsersQueue.getGenericType());

        //then
        assertThat(queueDeclaredType.isAssignableFrom(DummyUser.class)).isTrue();
    }

    public void shouldReturnDeclaredListTypeClassFromMethod(){
        //given
        Method dummyUsersList = getMethodFromClass(ReflectionTestModel.class, "getDummyUserList");

        //when
        Class arrayDeclaredType = getGenericTypeClass(dummyUsersList.getGenericReturnType());

        //then
        assertThat((arrayDeclaredType.isAssignableFrom(DummyUser.class))).isTrue();
    }

    public void shouldReturnDeclaredSetTypeClassFromMethod(){
        //given
        Method dummyUsersSet = getMethodFromClass(ReflectionTestModel.class, "getDummyUserSet");

        //when
        Class setDeclaredType = getGenericTypeClass(dummyUsersSet.getGenericReturnType());

        //then
        assertThat((setDeclaredType.isAssignableFrom(DummyUser.class))).isTrue();
    }

    public void shouldReturnDeclaredQueueTypeClassFromMethod(){
        //given
        Method dummyUsersList = getMethodFromClass(ReflectionTestModel.class, "getDummyUserList");

        //when
        Class arrayDeclaredType = getGenericTypeClass(dummyUsersList.getGenericReturnType());

        //then
        assertThat((arrayDeclaredType.isAssignableFrom(DummyUser.class))).isTrue();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFieldTypeIsGetByGetTypeMethod() {
        //given
        Field dummyUsersList = getFieldFromClass(ReflectionTestModel.class, "dummyUserList");

        //when
        getGenericTypeClass(dummyUsersList.getType());

        //then
        //should be thrown exception IlleaglArgumentException

    }

}
