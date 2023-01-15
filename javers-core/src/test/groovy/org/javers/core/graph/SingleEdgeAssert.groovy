package org.javers.core.graph

import org.fest.assertions.api.Assertions

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
