package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.ChangeAssert
import org.javers.core.model.SnapshotEntity
import spock.lang.Unroll

import static java.util.Optional.of
import static org.javers.repository.jql.InstanceIdDTO.instanceId

/**
 * @author bartosz.walacik
 */
class OptionalChangeAppenderTest extends AbstractDiffTest {

    @Unroll
    def "should not append change for equal optional Values (#leftOptional,#rightOptional)"(){
        given:
        def left =  new SnapshotEntity(optionalInteger: leftOptional)
        def right = new SnapshotEntity(optionalInteger: rightOptional)
        def property = getProperty(SnapshotEntity, "optionalInteger")

        when:
        def change = new OptionalChangeAppender()
                .calculateChanges(realNodePair(left, right), property)
        then:
        !change

        where:
        leftOptional     | rightOptional
        Optional.empty() | Optional.empty()
        Optional.empty() | null
        of(2)            | of(2)
    }

    @Unroll
    def "should append ReferenceChange for optional Entity references (#leftOptional,#rightOptional)"() {
        def left =  new SnapshotEntity(optionalEntity: of(new SnapshotEntity(id:2)))
        def right = new SnapshotEntity(optionalEntity: of(new SnapshotEntity(id:3)))
        def property = getProperty(SnapshotEntity, "optionalEntity")

        when:
        def change = new OptionalChangeAppender()
                .calculateChanges(realNodePair(left, right), property)

        then:
        ChangeAssert.assertThat(change)
                    .hasInstanceId(SnapshotEntity, 1)
        change.leftReferencce == expectedLeftRef
        change.rightReferencce == expectedRightRef

        where:
        leftOptional                 | rightOptional                | expectedLeftRef               | expectedRightRef
        Optional.empty()             | of(new SnapshotEntity(id:2)) | null                          | instanceId(2, SnapshotEntity)
        null                         | of(new SnapshotEntity(id:2)) | null                          | instanceId(2, SnapshotEntity)
        of(new SnapshotEntity(id:1)) | of(new SnapshotEntity(id:2)) | instanceId(1, SnapshotEntity) | instanceId(2, SnapshotEntity)
    }

    @Unroll
    def "should append ValueChange for optional Values (#leftOptional,#rightOptional)"(){
        given:
        def left =  new SnapshotEntity(optionalInteger: leftOptional)
        def right = new SnapshotEntity(optionalInteger: rightOptional)
        def property = getProperty(SnapshotEntity, "optionalInteger")

        when:
        def change = new OptionalChangeAppender()
                .calculateChanges(realNodePair(left, right), property)
        then:
        change.left == leftOptional
        change.right == leftOptional

        where:
        leftOptional     | rightOptional
        Optional.empty() | of(2)
        null             | of(2)
        of(1)            | of(2)
    }
}
