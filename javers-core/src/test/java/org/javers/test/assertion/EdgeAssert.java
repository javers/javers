package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.object.graph.Edge;
import org.javers.model.object.graph.MultiEdge;
import org.javers.model.object.graph.SingleEdge;

/**
 * @author bartosz walacik
 */
public class EdgeAssert extends AbstractAssert<EdgeAssert, Edge> {

    private EdgeAssert(Edge actual) {
        super(actual, EdgeAssert.class);
    }

    public static EdgeAssert assertThat(Edge actual) {
        return new EdgeAssert(actual);
    }

    public EdgeAssert hasProperty(String expectedName) {
        Assertions.assertThat(actual.getProperty().getName()).isEqualTo(expectedName);
        return this;
    }

    public MultiEdgeAssert isMultiEdge(Object... expectedRefCdoId) {
        return isMultiEdge().refersToCdoWithIds(expectedRefCdoId);
    }

    public MultiEdgeAssert isMultiEdge() {
        Assertions.assertThat(actual instanceof MultiEdge);
        return MultiEdgeAssert.assertThat((MultiEdge)actual);
    }

    public SingleEdgeAssert isSingleEdge() {
        Assertions.assertThat(actual instanceof SingleEdge);
        return SingleEdgeAssert.assertThat((SingleEdge)actual);
    }

    public SingleEdgeAssert isSingleEdgeTo(Object expectedRefCdoId) {
       return isSingleEdge().refersToCdoWithId(expectedRefCdoId);
    }
}
