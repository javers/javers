package org.javers.core.diff.appenders

import org.javers.core.diff.GraphPair
import org.javers.core.model.DummyUser

import static org.javers.core.diff.ChangeAssert.assertThat
import static org.javers.core.model.DummyUser.dummyUser

/**
 * @author Maciej Zasada
 */
class ObjectRemovedAppenderTest extends AbstractDiffAppendersTest {

    def "should append ObjectRemoved to diff"() {
        given:
        def cdoLeft = dummyUser("removed")
        def cdoRight = dummyUser("1")
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
        def left =  buildLiveGraph(dummyUser("removed").withDetails(5))
        def right = buildLiveGraph(dummyUser("1"))

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 2
        assertThat(changes[0]).isObjectRemoved()
        assertThat(changes[1]).isObjectRemoved()
    }

    def "should do nothing when graph has same node set"() {
        given:
        def left =  buildLiveGraph(dummyUser("1"))
        def right = buildLiveGraph(dummyUser("1"))

        when:
        def changes = new ObjectRemovedAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 0
    }

}
