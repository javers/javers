package org.javers.core.diff.appenders

import groovy.transform.TypeChecked
import org.javers.common.collections.Sets
import org.javers.core.diff.AbstractDiffTest
import org.javers.core.model.DummyUser
import org.javers.model.domain.Diff
import org.javers.model.object.graph.ObjectNode
import static org.javers.test.assertion.DiffAssert.assertThat

/**
 * @author Maciej Zasada
 */
class ObjectRemovedAppenderTest extends AbstractDiffTest {

    def "should append removedObject to diff"() {
        given:
        Diff diff = new Diff("userId")

        Set<ObjectNode> previousGraph = Sets.asSet(buildDummyUserNode("0"))
        Set<ObjectNode> currentGraph = Sets.asSet()

        when:
        diff.addChanges(new ObjectRemovedAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasChangesCount(1)
        assertThat(diff).getChangeAtIndex(0).isObjectRemoved().hasCdoId("0").hasEntityTypeOf(DummyUser.class).hasParentEqualTo(diff)
    }

    def "should append removedObjects to diff"() {
        given:
        Diff diff = new Diff("userId")

        Set<ObjectNode> previousGraph = Sets.asSet(buildDummyUserNode("2"), buildDummyUserNode("0"), buildDummyUserNode("1"))
        Set<ObjectNode> currentGraph = Sets.asSet(buildDummyUserNode("0"))

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

        Set<ObjectNode> previousGraph = Sets.asSet(buildDummyUserNode("1"))
        Set<ObjectNode> currentGraph = Sets.asSet(buildDummyUserNode("1"))

        when:
        diff.addChanges(new ObjectRemovedAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasChangesCount(0)
    }

}
