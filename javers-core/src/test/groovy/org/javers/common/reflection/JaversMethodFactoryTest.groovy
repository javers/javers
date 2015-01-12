package org.javers.common.reflection

import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class JaversMethodFactoryTest extends Specification {

    def "should return distinct method keys"() {
        given:

        when:
        def aKey = JaversMethodFactory.methodKey(ReflectionTestClass.class.getMethod("Aa", String.class))
        def bKey = JaversMethodFactory.methodKey(ReflectionTestClass.class.getMethod("BB", String.class))

        println("aKey $aKey")
        println("bKey $bKey")

        then:
        aKey != bKey
    }
}
