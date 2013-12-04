package org.javers.model.mapping.type

import org.javers.common.reflection.ReflectionTestModel
import org.javers.core.model.DummyUser
import spock.lang.Specification

import java.lang.reflect.Field

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass
import static org.javers.common.reflection.ReflectionUtil.getParametrizedTypeFirstArgument

/**
 * @author bartosz walacik
 */
class CollectionTypeTest extends Specification{
    class Dummy <T> {
        Set noGeneric
        Set<?> wildcardGeneric
        Set<T> parametrizedGeneric
        Set<String> genericWithArgument
    }

    def "should not be generic if baseJavaType is not generic"(){
        given:
        Field noGeneric = getFieldFromClass(Dummy, "noGeneric")

        when:
        CollectionType cType = new CollectionType(noGeneric.getType())

        then:
        cType.baseJavaType == Set
    }
}
