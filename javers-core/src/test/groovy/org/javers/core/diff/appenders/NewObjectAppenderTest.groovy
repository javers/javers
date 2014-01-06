package org.javers.core.diff.appenders

import org.javers.common.collections.Sets
import org.javers.core.diff.AbstractDiffTest
import org.javers.core.model.DummyUser
import org.javers.core.diff.Diff
import org.javers.model.object.graph.ObjectNode
import static org.javers.core.diff.DiffAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author Maciej Zasada
 */
class NewObjectAppenderTest extends AbstractDiffTest {

    def "should append newObject to diff"() {
        given:
        Diff diff = new Diff("userId")

        def node = buildGraph(dummyUser().withName("1").build())
        Set<ObjectNode> previousGraph = Sets.asSet()
        Set<ObjectNode> currentGraph = Sets.asSet(node)

        when:
        diff.addChanges(new NewObjectAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasSize(1)
        assertThat(diff).getChangeAtIndex(0)
                        .isNewObject()
                        .hasCdoId("1")
                        .hasEntityTypeOf(DummyUser)
                        //.hasParentEqualTo(diff)
                        .hasAffectedCdo(node)
    }

    def "should append newObjects to diff"() {
        given:
        Diff diff = new Diff("userId")

        Set<ObjectNode> previousGraph = Sets.asSet(buildGraph(dummyUser().withName("1").build()))
        Set<ObjectNode> currentGraph = Sets.asSet(
                buildGraph(dummyUser().withName("3").build()),
                buildGraph(dummyUser().withName("2").build()),
                buildGraph(dummyUser().withName("1").build()))

        when:
        diff.addChanges(new NewObjectAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasSize(2)
        assertThat(diff).getChangeAtIndex(0).isNewObject()
        assertThat(diff).getChangeAtIndex(1).isNewObject()
    }

    def "should do nothing when graph has same node set"() {
        given:
        Diff diff = new Diff("userId")

        Set<ObjectNode> previousGraph = Sets.asSet(buildGraph(dummyUser().withName("1").build()))
        Set<ObjectNode> currentGraph = Sets.asSet(buildGraph(dummyUser().withName("1").build()))

        when:
        diff.addChanges(new NewObjectAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasSize(0)
    }
}
