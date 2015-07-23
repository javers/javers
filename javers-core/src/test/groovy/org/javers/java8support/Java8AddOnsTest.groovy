package org.javers.java8support

import org.javers.core.metamodel.type.ValueType
import org.javers.core.model.SnapshotEntity
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.diff.DiffAssert.assertThat

/**
 * @author bartosz.walacik
 */
class Java8AddOnsTest extends Specification {

    //@IgnoreIf({ !ReflectionUtil.isJava8runtime() })
    def "should register java.time.LocalDate and LocalDateTime as ValueTypes"(){
        given:
        def javers = javers().build()

        expect:
        javers.getTypeMapping(LocalDate) instanceof ValueType
        javers.getTypeMapping(LocalDateTime) instanceof ValueType
    }

    def "should support optional values in diff" (){
        given:
        def javers = javers().build()
        def left =  new SnapshotEntity(optionalInteger: Optional.empty())
        def right = new SnapshotEntity(optionalInteger: Optional.of(1))

        when:
        def diff = javers.compare(left,right)
        println(diff)

        then:
        assertThat(diff).hasValueChangeAt("optionalInteger", null, 1)
    }
}
