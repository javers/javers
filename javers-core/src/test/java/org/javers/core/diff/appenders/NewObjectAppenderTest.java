package org.javers.core.diff.appenders;

import java.util.HashSet;
import java.util.Set;

import org.javers.common.collections.Sets;
import org.javers.core.model.DummyUser;
import org.javers.model.domain.Diff;
import org.javers.model.object.graph.ObjectNode;
import org.javers.test.assertion.DiffAssert;
import org.testng.annotations.Test;

/**
 * @author Maciej Zasada
 */
@Test
public class NewObjectAppenderTest extends ChangeSetAppenderTest {

    private NewObjectAppender newObjectAppender = new NewObjectAppender();

    public void shouldAppendNewObjectToDiff() {
        // given:
        Diff diff = new Diff("userId");

        Set<ObjectNode> previousGraph = new HashSet<>();
        Set<ObjectNode> currentGraph = Sets.asSet(createObjectNodeWithId("1"), createObjectNodeWithId("2"));

        // when:
        newObjectAppender.append(diff, previousGraph, currentGraph);

        // then:
        DiffAssert diffAssert = DiffAssert.assertThat(diff).hasChangesCount(2);
        diffAssert.getChangeAtIndex(0).isNewObject();
        diffAssert.getChangeAtIndex(1).isNewObject();
    }

    public void shouldSkipPreviouslyExistingObjectsInDiff() {
        // given:
        Diff diff = new Diff("userId");

        Set<ObjectNode> previousGraph = Sets.asSet(createObjectNodeWithId("1"));
        Set<ObjectNode> currentGraph = Sets.asSet(createObjectNodeWithId("1"), createObjectNodeWithId("2"));

        // when:
        newObjectAppender.append(diff, previousGraph, currentGraph);

        // then:
        DiffAssert.assertThat(diff).hasChangesCount(1).getChangeAtIndex(0).isNewObject().hasCdoId("2").hasEntityTypeOf(DummyUser.class).hasParentEqualTo(diff);
    }

    public void shouldNotFindAnyNewObjectsForDiff() {
        // given:
        Diff diff = new Diff("userId");

        Set<ObjectNode> previousGraph = Sets.asSet(createObjectNodeWithId("1"));
        Set<ObjectNode> currentGraph = Sets.asSet(createObjectNodeWithId("1"));

        // when:
        newObjectAppender.append(diff, previousGraph, currentGraph);

        // then:
        DiffAssert.assertThat(diff).hasChangesCount(0);
    }
}
