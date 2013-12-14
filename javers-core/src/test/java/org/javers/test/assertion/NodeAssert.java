package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.mapping.Property;
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

    public NodeAssert hasCdoId(String expectedCdoId) {
        Assertions.assertThat(actual.getLocalCdoId()).isEqualTo(expectedCdoId);
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

    public EdgeAssert hasEdge(Property property) {
        return EdgeAssert.assertThat(actual.getEdge(property));
    }

    public NodeAssert hasNoEdges() {
        return hasEdges(0);
    }

    public NodeAssert and() {
        return this;
    }

    public SingleEdgeAssert hasSingleEdge(Property property) {
        return hasEdge(property).isSingleEdge();
    }

    public MultiEdgeAssert hasMultiEdge(Property property) {
        return hasEdge(property).isMultiEdge();
    }
}