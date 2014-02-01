package org.javers.test.assertion

import org.javers.model.object.graph.MultiEdge

/**
 * @author bartosz walacik
 */
class MultiEdgeAssert {

    MultiEdge actual

    static assertThat = { MultiEdge actual ->
        new MultiEdgeAssert(actual: actual)
    }

    MultiEdgeAssert ofSize(int expectedSize) {
        assert actual.references.size() == expectedSize
        this
    }

    MultiEdgeAssert refersToCdoWithIds(def ... expectedRefCdoIds) {
        actual.references.collect { it.globalCdoId.cdoId }.with {
            assert it.size() == expectedRefCdoIds.size()
            assert it.containsAll(expectedRefCdoIds)
        }
        this
    }

    NodeAssert andTargetNode(String expectedTargetCdoId) {
        NodeAssert.assertThat(actual.references.find { it.globalCdoId.cdoId == expectedTargetCdoId })
    }
}
