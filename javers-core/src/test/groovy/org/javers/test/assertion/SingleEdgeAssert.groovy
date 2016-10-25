package org.javers.test.assertion

import org.fest.assertions.api.Assertions
import org.javers.core.graph.NodeAssert
import org.javers.core.graph.SingleEdge

/**
 * @author bartosz walacik
 */
class SingleEdgeAssert {

    SingleEdge actual

     static SingleEdgeAssert assertThat(SingleEdge actual) {
        return new SingleEdgeAssert(actual: actual)
    }

     SingleEdgeAssert refersToCdoWithId(Object expectedCdoId) {
        Assertions.assertThat(actual.reference).isNotNull()
        Assertions.assertThat(actual.reference.getCdoId())
                .isEqualTo(expectedCdoId)
        return this
    }

     NodeAssert andTargetNode() {
        Assertions.assertThat(actual.reference).isNotNull()
        return NodeAssert.assertThat(actual.referencedNode)
    }
}
