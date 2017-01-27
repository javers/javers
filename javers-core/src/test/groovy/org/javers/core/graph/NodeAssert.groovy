package org.javers.core.graph

import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.test.assertion.MultiEdgeAssert
import org.javers.test.assertion.SingleEdgeAssert

/**
 * @author bartosz walacik
 */
class NodeAssert {

    ObjectNode actual

    static assertThat = { ObjectNode actual ->
        new NodeAssert(actual: actual)
    }

    NodeAssert hasCdoId(def expectedLocalCdoId) {
        assert actual.globalId instanceof InstanceId
        assert actual.globalId.cdoId == expectedLocalCdoId
        this
    }

    NodeAssert hasGlobalId(def expectedGlobalId) {
        assert actual.globalId.value() == expectedGlobalId.value()
        this
    }

    NodeAssert hasInstanceId(Class expectedSourceClass, def expectedLocalCdoId) {
        actual.globalId.with {
            assert it instanceof InstanceId
            assert it.typeName == expectedSourceClass.name
            assert it.cdoId == expectedLocalCdoId
            it
        }
        this
    }

    public NodeAssert hasValueObjectId(String value){
        assert actual.globalId.value() == value
        this
    }

    public NodeAssert hasOwnerId(String value){
        assert actual.globalId.ownerId.value() == value
        this
    }

    public NodeAssert hasGlobalIdValue(String expectedValue) {
        actual.globalId.value() == expectedValue
        this
    }

    NodeAssert hasUnboundedValueObjectId(Class expectedSourceClass) {
        assert actual.globalId.typeName == expectedSourceClass.name
        this
    }

    NodeAssert hasEdges(int expectedSize) {
        assert actual.edges.size() == expectedSize
        this
    }

    EdgeAssert hasEdge(String edgeName) {
        def edge = actual.getEdge(edgeName)
        assert edge
        EdgeAssert.assertThat(edge)
    }

    NodeAssert hasCdo(def cdo) {
        assert cdo == actual.wrappedCdo().get()
        this
    }

    NodeAssert hasNoEdges() { hasEdges(0) }

    NodeAssert and() { this }

    SingleEdgeAssert hasSingleEdge(String edgeName) {
        hasEdge(edgeName).isSingleEdge()
    }

    MultiEdgeAssert hasMultiEdge(String edgeName) {
        hasEdge(edgeName).isMultiEdge()
    }

    NodeAssert isSnapshot() {
        assert actual.cdo.class == CdoSnapshot
        this
    }
}
