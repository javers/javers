package org.javers.test.assertion

import org.fest.assertions.api.Assertions
import org.javers.model.object.graph.SingleEdge

/**
 * @author bartosz walacik
 */
class SingleEdgeAssert {

    SingleEdge actual

     static SingleEdgeAssert assertThat(SingleEdge actual) {
        return new SingleEdgeAssert(actual: actual)
    }

     SingleEdgeAssert refersToCdoWithId(Object expectedCdoId) {
        Assertions.assertThat(actual.getReference()).isNotNull()
        Assertions.assertThat(actual.getReference().getGlobalCdoId().getCdoId())
                .isEqualTo(expectedCdoId)
        return this
    }

     NodeAssert andTargetNode() {
        Assertions.assertThat(actual.getReference()).isNotNull()
        return NodeAssert.assertThat(actual.getReference())
    }
}
