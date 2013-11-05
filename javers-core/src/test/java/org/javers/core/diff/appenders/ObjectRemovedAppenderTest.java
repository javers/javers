package org.javers.core.diff.appenders;

import java.util.HashSet;
import java.util.Set;

import org.javers.common.collections.Sets;
import org.javers.core.model.DummyUser;
import org.javers.model.domain.Diff;
import org.javers.model.object.graph.ObjectNode;
import org.javers.test.assertion.DiffAssert;
import org.junit.Test;

/**
 * @author Maciej Zasada
 */
public class ObjectRemovedAppenderTest extends ChangeSetAppenderTest {

    private ObjectRemovedAppender objectRemovedAppender = new ObjectRemovedAppender();

    @Test
    public void shouldAppendRemovedObjectToDiff() {
        // given:
        Diff diff = new Diff("userId");

        Set<ObjectNode> previousGraph = Sets.asSet(createObjectNodeWithId("1"), createObjectNodeWithId("2"));
        Set<ObjectNode> currentGraph = new HashSet<>();

        // when:
        objectRemovedAppender.append(diff, previousGraph, currentGraph);

        // then:
        DiffAssert diffAssert = DiffAssert.assertThat(diff).hasChangesCount(2);
        diffAssert.getChangeAtIndex(0).isObjectRemoved();
        diffAssert.getChangeAtIndex(1).isObjectRemoved();
    }

    @Test
    public void shouldSkipPreviouslyExistingObjectsInDiff() {
        // given:
        Diff diff = new Diff("userId");

        Set<ObjectNode> previousGraph = Sets.asSet(createObjectNodeWithId("1"), createObjectNodeWithId("2"));
        Set<ObjectNode> currentGraph = Sets.asSet(createObjectNodeWithId("1"));

        // when:
        objectRemovedAppender.append(diff, previousGraph, currentGraph);

        // then:
        DiffAssert.assertThat(diff).hasChangesCount(1).getChangeAtIndex(0).isObjectRemoved().hasCdoId("2")
                .hasEntityTypeOf(DummyUser.class).hasParentEqualTo(diff);
    }

    @Test
    public void shouldNotFindAnyRemovedObjectsForDiff() {
        // given:
        Diff diff = new Diff("userId");

        Set<ObjectNode> previousGraph = Sets.asSet(createObjectNodeWithId("1"));
        Set<ObjectNode> currentGraph = Sets.asSet(createObjectNodeWithId("1"));

        // when:
        objectRemovedAppender.append(diff, previousGraph, currentGraph);

        // then:
        DiffAssert.assertThat(diff).hasChangesCount(0);
    }

}
