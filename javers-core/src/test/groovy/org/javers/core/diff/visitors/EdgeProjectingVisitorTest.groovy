package org.javers.core.diff.visitors

import org.javers.core.model.DummyUser
import org.javers.model.mapping.Property
import org.javers.model.object.graph.MultiEdge
import org.javers.model.object.graph.ObjectNode
import spock.lang.Specification
import org.javers.model.object.graph.ObjectGraphBuilder
import static org.javers.test.EntityManagerFactory.createWithEntities
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author Maciej Zasada
 */
class EdgeProjectingVisitorTest extends Specification {

    EdgeProjectingVisitor edgeProjectingVisitor = new EdgeProjectingVisitor()

    def "should project left edge to the right edge"() {
        given:
        def leftEdges = [multiEdge([node("1"), node("2")])]
        def rightEdges = [multiEdge([])]

        when:
        edgeProjectingVisitor.visit(leftEdges, rightEdges)

        then:
        edgeProjectingVisitor.multiEdgesProjection == [(leftEdges[0]): (rightEdges[0])]
    }

    private MultiEdge multiEdge(references) {
        new MultiEdge([:] as Property).with { it.references = references; it }
    }

    private ObjectNode node(String id) {
        ObjectGraphBuilder objectGraphBuilder = new ObjectGraphBuilder(createWithEntities(DummyUser));
        objectGraphBuilder.buildGraph(dummyUser().withName(id).build())
    }
}
