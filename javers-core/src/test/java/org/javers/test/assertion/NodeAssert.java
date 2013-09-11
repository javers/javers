package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.core.Condition;
import org.javers.model.object.graph.Edge;
import org.javers.model.object.graph.MultiEdge;
import org.javers.model.object.graph.ObjectNode;
import org.javers.model.object.graph.SingleEdge;

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

    public NodeAssert hasCdoWithId(String expectedCdoId) {
        Assertions.assertThat(actual.getCdoId()).isEqualTo(expectedCdoId);
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

    public NodeAssert hasNoEdges() {
        return hasEdges(0);
    }

    public NodeAssert haveAtLeastMultiEdge(int numberOfOccurrence) {
        Assertions.assertThat(actual.getEdges()).haveAtLeast(numberOfOccurrence, new Condition<Edge>() {
            @Override
            public boolean matches(Edge value) {
                return value instanceof MultiEdge;
            }
        });
        return this;
    }

    public NodeAssert haveAtLeastSingleEdge(int numberOfOccurrence) {
        Assertions.assertThat(actual.getEdges()).haveAtLeast(numberOfOccurrence, new Condition<Edge>() {
            @Override
            public boolean matches(Edge value) {
                return value instanceof SingleEdge;
            }
        });
        return this;
    }
}