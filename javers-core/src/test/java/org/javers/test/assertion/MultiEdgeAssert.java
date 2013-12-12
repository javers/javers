package org.javers.test.assertion;

import com.google.common.collect.Lists;
import org.fest.assertions.api.AbstractAssert;
import org.javers.model.object.graph.MultiEdge;
import org.javers.model.object.graph.ObjectNode;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class MultiEdgeAssert extends AbstractAssert<MultiEdgeAssert, MultiEdge> {
    private MultiEdgeAssert(MultiEdge actual) {
        super(actual, MultiEdgeAssert.class);
    }

    public static MultiEdgeAssert assertThat(MultiEdge actual) {
        return new MultiEdgeAssert(actual);
    }

    public MultiEdgeAssert ofSize(int expectedSize) {
        Assertions.assertThat(actual.getReferences()).hasSize(expectedSize);
        return this;
    }

    public MultiEdgeAssert refersToCdoWithIds(Object... expectedRefCdoIds) {
        List actualCdoIds = Lists.newArrayList();
        for (ObjectNode node : actual.getReferences()) {
            actualCdoIds.add(node.getLocalCdoId());
        }

        Assertions.assertThat(actualCdoIds).containsOnly(expectedRefCdoIds);

        return this;
    }

    public NodeAssert andTargetNode(String expectedTargetCdoId) {
        Assertions.assertThat(actual.getReference(expectedTargetCdoId)).isNotNull();
        return NodeAssert.assertThat(actual.getReference(expectedTargetCdoId));
    }
}
