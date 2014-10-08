package org.javers.core.graph

import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.test.assertion.EdgeAssert
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
        assert actual.globalId == expectedGlobalId
        this
    }

    NodeAssert hasInstanceId(Class expectedSourceClass, def expectedLocalCdoId) {
        actual.globalId.with {
            assert it instanceof InstanceId
            assert it.cdoClass.sourceClass == expectedSourceClass
            assert it.cdoId == expectedLocalCdoId
            it
        }
        this
    }

    public NodeAssert hasValueObjectId(Class expectedManagedClass, Object owner, String expectedFragment) {
        ValueObjectId valueObjectId = actual.globalId

        assert valueObjectId.cdoClass.sourceClass == expectedManagedClass
        assert valueObjectId.cdoId == null
        assert valueObjectId.fragment == expectedFragment
        assert (valueObjectId.ownerId as InstanceId).idEquals(owner)
        this
    }

    NodeAssert hasUnboundedValueObjectId(Class expectedSourceClass) {
        assert actual.globalId.cdoClass.sourceClass == expectedSourceClass
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
