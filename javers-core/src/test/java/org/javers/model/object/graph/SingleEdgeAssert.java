package org.javers.model.object.graph;

import org.fest.assertions.api.AbstractAssert;
import org.javers.test.assertion.Assertions;

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

    public SingleEdgeAssert refersToCdoWithId(String expectedCdoId) {
        Assertions.assertThat(actual.getReferences()).hasSize(1);
        Assertions.assertThat(actual.getReferences().get(0).getCdoId()).isEqualTo(expectedCdoId);
        return this;
    }
}
