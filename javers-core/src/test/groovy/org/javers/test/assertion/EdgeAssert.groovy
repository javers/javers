package org.javers.test.assertion

import org.javers.core.graph.Edge
import org.javers.core.graph.MultiEdge
import org.javers.core.graph.SingleEdge


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
        assert actual instanceof MultiEdge
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
