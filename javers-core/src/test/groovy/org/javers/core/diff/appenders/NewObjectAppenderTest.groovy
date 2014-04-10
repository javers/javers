package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.GraphPair
import org.javers.core.model.DummyUser

import static org.javers.core.diff.ChangeAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author Maciej Zasada
 */
class NewObjectAppenderTest extends AbstractDiffTest {

    def "should append newObject to diff"() {
        given:
        def left =  buildGraph(dummyUser().withName("1").build())
        def right = buildGraph(dummyUser().withName("added").build())

        when:
        def changes = new NewObjectAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 1
        assertThat(changes[0])
                        .isNewObject()
                        .hasCdoId("added")
                        .hasEntityTypeOf(DummyUser)
                        .hasAffectedCdo(right.wrappedCdo().get())
    }

    def "should append newObjects to diff"() {
        given:
        def left =  buildGraph(dummyUser().withName("1").build())
        def right = buildGraph(dummyUser().withName("added").withDetails(5).build())

        when:
        def changes = new NewObjectAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 2
        assertThat(changes[0]).isNewObject()
        assertThat(changes[1]).isNewObject()
    }

    def "should do nothing when graph has same node set"() {
        given:
        def left =  buildGraph(dummyUser().withName("1").build())
        def right = buildGraph(dummyUser().withName("1").build())

        when:
        def changes = new NewObjectAppender().getChangeSet(new GraphPair(left, right))

        then:
        changes.size() == 0
    }
}
