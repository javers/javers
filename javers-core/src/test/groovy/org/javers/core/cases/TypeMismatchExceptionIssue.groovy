package org.javers.core.cases

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.JaversBuilder
import spock.lang.Specification

import static TypeMismatchExceptionClasses.*

/**
 * @author bartosz walacik
 */
class TypeMismatchExceptionIssue extends Specification{
    def "should throw proper exception when comparing different types"() {
        given:
        def javers = JaversBuilder.javers().build()

        def first = new Holder(new B0())
        def second = new Holder(new B1())

        when:
        javers.compare(first, second)

        then:
        def e = thrown(JaversException)
        e.code == JaversExceptionCode.TYPE_MISMATCH
    }
}
