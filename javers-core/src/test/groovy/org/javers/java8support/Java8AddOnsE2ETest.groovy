package org.javers.java8support

import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.type.ValueType
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.ValueObjectIdDTO
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.diff.DiffAssert.assertThat

/**
 * @author bartosz.walacik
 */
class Java8AddOnsE2ETest extends Specification {

    //@IgnoreIf({ !ReflectionUtil.isJava8runtime() })
    def "should register java.time.LocalDate and LocalDateTime as ValueTypes"(){
        given:
        def javers = javers().build()

        expect:
        javers.getTypeMapping(LocalDate) instanceof ValueType
        javers.getTypeMapping(LocalDateTime) instanceof ValueType
    }

    @Unroll
    def "should support optional values (#leftOptional, #rightOptional) changes" (){
        given:
        def javers = javers().build()
        def left =  new SnapshotEntity(optionalInteger: leftOptional)
        def right = new SnapshotEntity(optionalInteger: rightOptional)

        when:
        def diff = javers.compare(left,right)

        then:
        assertThat(diff).hasValueChangeAt("optionalInteger", leftOptional, rightOptional)

        where:
        leftOptional     | rightOptional
        Optional.empty() | Optional.of(1)
        Optional.of(1)   | Optional.of(2)
    }

    def "should support value changes in optional ValueObjects"(){
        def javers = javers().build()
        def left =  new SnapshotEntity(optionalValueObject: Optional.of(new DummyAddress("New York")) )
        def right = new SnapshotEntity(optionalValueObject: Optional.of(new DummyAddress("Paris")) )

        when:
        def diff = javers.compare(left,right)

        then:
        diff.changes.size() == 1
        def change = diff.changes[0]
        change.affectedGlobalId == ValueObjectIdDTO.valueObjectId(1, SnapshotEntity, "optionalValueObject")
        change instanceof ValueChange
        change.left == "New York"
        change.right == "Paris"
    }
}
