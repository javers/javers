package org.javers.java8support

import org.javers.common.reflection.ReflectionUtil
import org.javers.core.metamodel.type.ValueType
import spock.lang.IgnoreIf
import spock.lang.Specification

import java.time.LocalDate

import static org.javers.core.JaversBuilder.javers

/**
 * @author bartosz.walacik
 */
class Java8AddOnsTest extends Specification {

    @IgnoreIf({ !ReflectionUtil.isJava8runtime() })
    def "should register java.time.LocalDate as ValueType"(){
        given:
        def javers = javers().build()

        when:
        def jType = javers.getTypeMapping(LocalDate)

        then:
        jType instanceof ValueType
    }
}
