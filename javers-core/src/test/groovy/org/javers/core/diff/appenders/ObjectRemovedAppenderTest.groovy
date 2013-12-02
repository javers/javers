package org.javers.core.diff.appenders

import org.javers.common.collections.Sets
import org.javers.core.diff.AbstractDiffTest
import org.javers.core.model.DummyUser
import org.javers.model.domain.Diff
import org.javers.model.object.graph.ObjectNode
import static org.javers.test.assertion.DiffAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author Maciej Zasada
 */
class ObjectRemovedAppenderTest extends AbstractDiffTest {

    def "should append removedObject to diff"() {
        given:
        Diff diff = new Diff("userId")

        def node = buildGraph(dummyUser().withName("0").build())
        Set<ObjectNode> previousGraph = Sets.asSet(node)
        Set<ObjectNode> currentGraph = Sets.asSet()

        when:
        diff.addChanges(new ObjectRemovedAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasChangesCount(1)
        assertThat(diff).getChangeAtIndex(0).isObjectRemoved().hasCdoId("0").hasEntityTypeOf(DummyUser.class).hasParentEqualTo(diff).hasCdo(node.cdo.wrappedCdo)
    }

    def "should append removedObjects to diff"() {
        given:
        Diff diff = new Diff("userId")

        Set<ObjectNode> previousGraph = Sets.asSet(
                buildGraph(dummyUser().withName("2").build()),
                buildGraph(dummyUser().withName("0").build()),
                buildGraph(dummyUser().withName("1").build()))
        Set<ObjectNode> currentGraph = Sets.asSet(buildGraph(dummyUser().withName("0").build()))

        when:
        diff.addChanges(new ObjectRemovedAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasChangesCount(2)
        assertThat(diff).getChangeAtIndex(0).isObjectRemoved()
        assertThat(diff).getChangeAtIndex(1).isObjectRemoved()
    }

    def "should do nothing when graph has same node set"() {
        given:
        Diff diff = new Diff("userId")

        Set<ObjectNode> previousGraph = Sets.asSet(buildGraph(dummyUser().withName("1").build()))
        Set<ObjectNode> currentGraph = Sets.asSet(buildGraph(dummyUser().withName("1").build()))

        when:
        diff.addChanges(new ObjectRemovedAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasChangesCount(0)
    }

}
