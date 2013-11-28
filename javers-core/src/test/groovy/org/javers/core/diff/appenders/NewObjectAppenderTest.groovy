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
class NewObjectAppenderTest extends AbstractDiffTest {

    def "should append newObject to diff"() {
        given:
        Diff diff = new Diff("userId")

        Set<ObjectNode> previousGraph = Sets.asSet()
        Set<ObjectNode> currentGraph = Sets.asSet(buildDummyUserNode("1"))

        when:
        diff.addChanges(new NewObjectAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasChangesCount(1)
        assertThat(diff).getChangeAtIndex(0).isNewObject().hasCdoId("1").hasEntityTypeOf(DummyUser.class).hasParentEqualTo(diff)
    }

    def "should append newObjects to diff"() {
        given:
        Diff diff = new Diff("userId")

        Set<ObjectNode> previousGraph = Sets.asSet(buildDummyUserNode("1"))
        Set<ObjectNode> currentGraph = Sets.asSet(buildDummyUserNode("3"), buildDummyUserNode("2"), buildDummyUserNode("1"))

        when:
        diff.addChanges(new NewObjectAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasChangesCount(2)
        assertThat(diff).getChangeAtIndex(0).isNewObject()
        assertThat(diff).getChangeAtIndex(1).isNewObject()
    }

    def "should do nothing when graph has same node set"() {
        given:
        Diff diff = new Diff("userId")

        Set<ObjectNode> previousGraph = Sets.asSet(buildDummyUserNode("1"))
        Set<ObjectNode> currentGraph = Sets.asSet(buildDummyUserNode("1"))

        when:
        diff.addChanges(new NewObjectAppender().getChangeSet(previousGraph, currentGraph))

        then:
        assertThat(diff).hasChangesCount(0)
    }
}
