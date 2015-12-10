package org.javers.common.reflection

import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class JaversMethodFactoryTest extends Specification {

    //TODO check if this is relevant
    def "should return distinct method keys"() {
        given:

        when:
        def aKey = JaversMethodFactory.methodKey(ReflectionTestClass.getMethod("Aa", String))
        def bKey = JaversMethodFactory.methodKey(ReflectionTestClass.getMethod("BB", String))

        println("aKey $aKey")
        println("bKey $bKey")

        then:
        aKey != bKey
    }
}
