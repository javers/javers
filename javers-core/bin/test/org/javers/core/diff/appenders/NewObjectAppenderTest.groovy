package org.javers.core.diff.appenders

import org.javers.core.diff.GraphPair
import org.javers.core.model.DummyUser

import static org.javers.core.diff.ChangeAssert.assertThat
import static org.javers.core.model.DummyUser.dummyUser

/**
 * @author Maciej Zasada
 */
class NewObjectAppenderTest extends AbstractDiffAppendersTest {

    def "should append one newObject to diff"() {
        given:
        def cdoLeft = dummyUser("1")
        def cdoRight = dummyUser("added")
        def left =  buildLiveGraph(cdoLeft)
        def right = buildLiveGraph(cdoRight)

        when:
        def changes = new NewObjectAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 1
        assertThat(changes[0])
                        .isNewObject()
                        .hasCdoId("added")
                        .hasEntityTypeOf(DummyUser)
                        .hasAffectedCdo(cdoRight)
    }

    def "should append two newObjects to diff"() {
        given:
        def left =  buildLiveGraph(dummyUser("1"))
        def right = buildLiveGraph(dummyUser("added").withDetails(5))

        when:
        def changes = new NewObjectAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 2
        assertThat(changes[0]).isNewObject()
        assertThat(changes[1]).isNewObject()
    }

    def "should do nothing when graph has same node set"() {
        given:
        def left =  buildLiveGraph(dummyUser("1"))
        def right = buildLiveGraph(dummyUser("1"))

        when:
        def changes = new NewObjectAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 0
    }
}
