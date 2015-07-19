package org.javers.java8support

import org.javers.common.reflection.ReflectionUtil
import org.javers.core.metamodel.type.ValueType
import spock.lang.IgnoreIf
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static org.javers.core.JaversBuilder.javers

/**
 * @author bartosz.walacik
 */
class Java8AddOnsTest extends Specification {

    @IgnoreIf({ !ReflectionUtil.isJava8runtime() })
    def "should register java.time.LocalDate and LocalDateTime as ValueTypes"(){
        given:
        def javers = javers().build()

        expect:
        javers.getTypeMapping(LocalDate) instanceof ValueType
        javers.getTypeMapping(LocalDateTime) instanceof ValueType
    }
}
