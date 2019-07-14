package org.javers.core.diff.appenders

import org.javers.core.diff.ChangeAssert
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.SnapshotEntity
import spock.lang.Unroll
import static java.util.Optional.*
import static org.javers.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz.walacik
 */
class OptionalChangeAppenderTest extends AbstractDiffAppendersTest {

    @Unroll
    def "should not append change for equal optional Values (#leftOptional,#rightOptional)"(){
        given:
        def left =  new SnapshotEntity(optionalInteger: leftOptional)
        def right = new SnapshotEntity(optionalInteger: rightOptional)
        def property = getProperty(SnapshotEntity, "optionalInteger")

        when:
        def change = optionalChangeAppender()
                .calculateChanges(realNodePair(left, right), property)
        then:
        !change

        where:
        leftOptional  | rightOptional
        empty()       | empty()
        empty()       | null
        of(2)         | of(2)
    }

    @Unroll
    def "should append ReferenceChange for optional Entity references (#leftOptional,#rightOptional)"() {
        def left =  new SnapshotEntity(optionalEntity: leftOptional)
        def right = new SnapshotEntity(optionalEntity: rightOptional)

        def property = getProperty(SnapshotEntity, "optionalEntity")

        when:
        def change = optionalChangeAppender()
                .calculateChanges(realNodePair(left, right), property)

        then:
        ChangeAssert.assertThat(change)
                    .hasInstanceId(SnapshotEntity, 1)
        change instanceof ReferenceChange
        ReferenceChangeAssert.assertThat( change )
            .hasLeftReference( expectedLeftRef )
            .hasRightReference( expectedRightRef )

        change.getLeftObject() == expectedLeftObject
        change.getRightObject() == rightOptional

        where:
        expectedLeftObject           | leftOptional                 | rightOptional                | expectedLeftRef               | expectedRightRef
        empty()                      | empty()                      | of(new SnapshotEntity(id:2)) | null                          | instanceId(2, SnapshotEntity)
        empty()                      | null                         | of(new SnapshotEntity(id:2)) | null                          | instanceId(2, SnapshotEntity)
        of(new SnapshotEntity(id:1)) | of(new SnapshotEntity(id:1)) | of(new SnapshotEntity(id:2)) | instanceId(1, SnapshotEntity) | instanceId(2, SnapshotEntity)
    }

    @Unroll
    def "should append ValueChange for optional Values (#leftOptional,#rightOptional)"(){
        given:
        def left =  new SnapshotEntity(optionalInteger: leftOptional)
        def right = new SnapshotEntity(optionalInteger: rightOptional)
        def property = getProperty(SnapshotEntity, "optionalInteger")

        when:
        def change = optionalChangeAppender()
                .calculateChanges(realNodePair(left, right), property)
        then:
        change instanceof ValueChange
        change.left == expectedLeft
        change.right == rightOptional

        where:
        leftOptional     | rightOptional  | expectedLeft
        empty()          | of(2)          | empty()
        null             | of(2)          | empty()
        of(1)            | of(2)          | of(1)
    }
}
