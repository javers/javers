package org.javers.test.assertion

import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.model.object.graph.ObjectNode

/**
 * @author bartosz walacik
 */
class NodeAssert {

    ObjectNode actual

    static assertThat = { ObjectNode actual ->
        new NodeAssert(actual: actual)
    }

    NodeAssert hasCdoId(def expectedLocalCdoId) {
        assert actual.globalCdoId instanceof InstanceId
        assert actual.globalCdoId.cdoId == expectedLocalCdoId
        this
    }

    NodeAssert hasInstanceId(Class expectedSourceClass, def expectedLocalCdoId) {
        actual.globalCdoId.with {
            assert it instanceof InstanceId
            assert it.cdoClass.sourceClass == expectedSourceClass
            assert it.cdoId == expectedLocalCdoId
            it
        }
        this
    }

    public NodeAssert hasValueObjectId(Class expectedManagedClass, Object owner, String expectedFragment) {
        ValueObjectId valueObjectId = actual.globalCdoId

        assert valueObjectId.cdoClass.sourceClass == expectedManagedClass
        assert valueObjectId.cdoId == null
        assert valueObjectId.fragment == expectedFragment
        assert (valueObjectId.ownerId as InstanceId).idEquals(owner)
        this
    }

    NodeAssert hasUnboundedValueObjectId(Class expectedSourceClass) {
        assert actual.globalCdoId.cdoClass.sourceClass == expectedSourceClass
        this
    }

    NodeAssert hasEdges(int expectedSize) {
        assert actual.edges.size() == expectedSize
        this
    }

    EdgeAssert hasEdge(String edgeName) {
        EdgeAssert.assertThat(actual.edges.find { it.property.name == edgeName })
    }

    NodeAssert hasCdo(def cdo) {
        cdo == actual.cdo.wrappedCdo
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
}
