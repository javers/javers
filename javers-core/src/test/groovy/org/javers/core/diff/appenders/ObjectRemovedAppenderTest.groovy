package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.GraphPair
import org.javers.core.model.DummyUser

import static org.javers.core.diff.ChangeAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author Maciej Zasada
 */
class ObjectRemovedAppenderTest extends AbstractDiffAppendersTest {

    def "should append ObjectRemoved to diff"() {
        given:
        def cdoLeft = dummyUser().withName("removed").build()
        def cdoRight = dummyUser().withName("1").build()
        def left =  buildLiveGraph(cdoLeft)
        def right = buildLiveGraph(cdoRight)

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 1
        assertThat(changes[0])
                    .isObjectRemoved()
                    .hasCdoId("removed")
                    .hasEntityTypeOf(DummyUser)
                    .hasAffectedCdo(cdoLeft)
    }

    def "should append 2 ObjectRemoved to diff"() {
        given:
        def left =  buildLiveGraph(dummyUser().withName("removed").withDetails(5).build())
        def right = buildLiveGraph(dummyUser().withName("1").build())

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 2
        assertThat(changes[0]).isObjectRemoved()
        assertThat(changes[1]).isObjectRemoved()
    }

    def "should do nothing when graph has same node set"() {
        given:
        def left =  buildLiveGraph(dummyUser().withName("1").build())
        def right = buildLiveGraph(dummyUser().withName("1").build())

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 0
    }

}
