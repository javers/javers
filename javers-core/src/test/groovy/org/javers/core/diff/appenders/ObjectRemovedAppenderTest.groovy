package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.GraphPair
import org.javers.core.model.DummyUser

import static org.javers.core.diff.ChangeAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author Maciej Zasada
 */
class ObjectRemovedAppenderTest extends AbstractDiffTest {

    def "should append ObjectRemoved to diff"() {
        given:
        def left =  buildGraph(dummyUser().withName("removed").build())
        def right = buildGraph(dummyUser().withName("1").build())

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 1
        assertThat(changes[0])
                    .isObjectRemoved()
                    .hasCdoId("removed")
                    .hasEntityTypeOf(DummyUser)
                    .hasAffectedCdo(left.wrappedCdo().get())
    }

    def "should append 2 ObjectRemoved to diff"() {
        given:
        def left =  buildGraph(dummyUser().withName("removed").withDetails(5).build())
        def right = buildGraph(dummyUser().withName("1").build())

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 2
        assertThat(changes[0]).isObjectRemoved()
        assertThat(changes[1]).isObjectRemoved()
    }

    def "should do nothing when graph has same node set"() {
        given:
        def left =  buildGraph(dummyUser().withName("1").build())
        def right = buildGraph(dummyUser().withName("1").build())

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 0
    }

}
