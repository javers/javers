package org.javers.common.reflection

import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class JaversMethodFactoryTest extends Specification {

    //TODO check if this is relevant
    def "should return distinct method keys"() {
        given:
        def keys = [:]
        when:
        keys.put("Aa", JaversMethodFactory.methodKey(ReflectionTestClass.getMethod("Aa", String)))
        keys.put("BB", JaversMethodFactory.methodKey(ReflectionTestClass.getMethod("BB", String)))
        keys.put("isOrOperation", JaversMethodFactory.methodKey(ReflectionTestClass.getMethod("isOrOperation", String)))
        keys.put("getCmdline", JaversMethodFactory.methodKey(ReflectionTestClass.getMethod("getCmdline", String)))

        keys.keySet().forEach {
            println it + " : " + keys[it]
        }

        then:
        new HashSet(keys.values()).size() == 4
    }
}
