package org.javers.core.graph

import org.javers.test.assertion.MultiEdgeAssert
import org.javers.test.assertion.SingleEdgeAssert


/**
 * @author bartosz walacik
 */
class EdgeAssert {

    Edge actual

    static assertThat = { Edge actual ->
        new EdgeAssert(actual: actual)
    }

    MultiEdgeAssert isMultiEdge(def ... expectedLocalIds) {
        isMultiEdge().refersToLocalIds(expectedLocalIds)
    }

    MultiEdgeAssert isMultiEdge() {
        assert MultiEdge.isAssignableFrom(actual.class)
        MultiEdgeAssert.assertThat(actual)
    }

    SingleEdgeAssert isSingleEdge() {
        assert actual instanceof SingleEdge
        SingleEdgeAssert.assertThat(actual)
    }

    SingleEdgeAssert isSingleEdgeTo(def expectedRefCdoId) {
        isSingleEdge().refersToCdoWithId(expectedRefCdoId)
    }
}
