package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.domain.InstanceId;
import org.javers.model.domain.UnboundedValueObjectId;
import org.javers.model.domain.ValueObjectId;
import org.javers.model.object.graph.Edge;
import org.javers.model.object.graph.ObjectNode;

/**
 * @author bartosz walacik
 */
public class NodeAssert extends AbstractAssert<NodeAssert, ObjectNode> {

    private NodeAssert(ObjectNode actual) {
        super(actual, NodeAssert.class);
    }

    public static NodeAssert assertThat(ObjectNode actual) {
        return new NodeAssert(actual);
    }

    public NodeAssert hasCdoId(Object expectedLocalCdoId) {
        Assertions.assertThat(actual.getGlobalCdoId()).isInstanceOf(InstanceId.class);
        InstanceId actualInstanceId = (InstanceId)actual.getGlobalCdoId();
        Assertions.assertThat(actualInstanceId.getCdoId()).isEqualTo(expectedLocalCdoId);
        return this;
    }

    public NodeAssert hasInstanceId(Class expectedSourceClass, Object expectedLocalCdoId) {

        Assertions.assertThat(actual.getGlobalCdoId()).isInstanceOf(InstanceId.class);
        InstanceId actualInstanceId = (InstanceId)actual.getGlobalCdoId();

        Assertions.assertThat(actualInstanceId.getCdoClass().getSourceClass()).isSameAs(expectedSourceClass);
        Assertions.assertThat(actualInstanceId.getCdoId()).isEqualTo(expectedLocalCdoId);
        return this;
    }

    public NodeAssert hasValueObjectId(Class expectedSourceClass, Object expectedLocalCdoId, String expectedFragment) {
        /*
        Assertions.assertThat(actual.getGlobalCdoId()).isInstanceOf(ValueObjectId.class);
        ValueObjectId vlueObjectId = (ValueObjectId)actual.getGlobalCdoId();

        Assertions.assertThat(vlueObjectId.getCdoClass().getSourceClass()).isSameAs(expectedSourceClass);
        Assertions.assertThat(vlueObjectId.getCdoId()).isEqualTo(expectedLocalCdoId);
        Assertions.assertThat(vlueObjectId.getFragment()).isEqualTo(expectedFragment);
        return this;
        */
        throw new IllegalStateException("not implemented");
    }

    public NodeAssert hasUnboundedValueObjectId(Class expectedSourceClass) {
        Assertions.assertThat(actual.getGlobalCdoId()).isInstanceOf(UnboundedValueObjectId.class);

        UnboundedValueObjectId unboundedValueObjectId = (UnboundedValueObjectId)actual.getGlobalCdoId();

        Assertions.assertThat(unboundedValueObjectId.getCdoClass().getSourceClass()).isSameAs(expectedSourceClass);

        return this;
    }

    public NodeAssert hasEdges(int expectedSize) {
        Assertions.assertThat(actual.getEdges())
                  .overridingErrorMessage("expected edges:"+expectedSize+" but was:"+actual.getEdges().size())
                  .hasSize(expectedSize);
        return this;
    }

    public EdgeAssert andFirstEdge() {
        Assertions.assertThat(actual.getEdges()).isNotEmpty();
        return EdgeAssert.assertThat(actual.getEdges().get(0));
    }

    public EdgeAssert hasEdge(String edgeName) {
        Assertions.assertThat(actual.getEdges()).overridingErrorMessage("no edges").isNotEmpty();
        for (Edge edge : actual.getEdges()) {
            if(edge.getProperty().getName().equals(edgeName)) {
                return EdgeAssert.assertThat(edge);
            }
        }
        Assertions.fail("no such edge: "+ edgeName);
        return null; // never happens, Assertions.fail is before this line
    }

    public NodeAssert hasCdo(Object cdo) {
        Assertions.assertThat(cdo).isSameAs(actual.getCdo().getWrappedCdo());
        return this;
    }

    public NodeAssert hasNoEdges() {
        return hasEdges(0);
    }

    public NodeAssert and() {
        return this;
    }

    public SingleEdgeAssert hasSingleEdge(String edgeName) {
        return hasEdge(edgeName).isSingleEdge();
    }

    public MultiEdgeAssert hasMultiEdge(String edgeName) {
        return hasEdge(edgeName).isMultiEdge();
    }
}