package org.javers.test.assertion

import org.javers.core.graph.MultiEdge
import org.javers.core.graph.NodeAssert
import org.javers.core.metamodel.object.CdoSnapshot

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

    MultiEdgeAssert refersToLocalIds(Object... localIds) {
        assert actual.references.collect { it.globalId.cdoId }.toSet() == localIds.toList().toSet()
        this
    }

    MultiEdgeAssert refersToGlobalIds(Object expectedRefCdoIds) {
        assert actual.references.collect { it.globalId.value() } ==
                expectedRefCdoIds.collect { it.value() }
        this
    }

    MultiEdgeAssert refersToSnapshots(){
        assert actual.references.each {
            assert it.cdo.class == CdoSnapshot
        }
        this
    }

    NodeAssert andTargetNode(String expectedTargetCdoId) {
        NodeAssert.assertThat(actual.references.find { it.globalId.cdoId == expectedTargetCdoId })
    }

    NodeAssert andFirstTargetNode (){
        NodeAssert.assertThat(actual.references[0])
    }

}
