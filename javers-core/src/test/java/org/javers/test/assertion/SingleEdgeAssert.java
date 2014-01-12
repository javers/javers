package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.object.graph.SingleEdge;

/**
 * @author bartosz walacik
 */
public class SingleEdgeAssert extends AbstractAssert<SingleEdgeAssert, SingleEdge> {

    private SingleEdgeAssert(SingleEdge actual) {
        super(actual, SingleEdgeAssert.class);
    }

    public static SingleEdgeAssert assertThat(SingleEdge actual) {
        return new SingleEdgeAssert(actual);
    }

    public SingleEdgeAssert refersToCdoWithId(Object expectedCdoId) {
        Assertions.assertThat(actual.getReference()).isNotNull();
        Assertions.assertThat(actual.getReference().getGlobalCdoId().getCdoId())
                  .isEqualTo(expectedCdoId);
        return this;
    }

    public NodeAssert andTargetNode() {
        Assertions.assertThat(actual.getReference()).isNotNull();
        return NodeAssert.assertThat(actual.getReference());
    }
}
