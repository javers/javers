package org.javers.core.diff.calculators

import org.javers.core.model.DummyUser
import org.javers.model.domain.GlobalCdoId
import org.javers.model.domain.changeType.ReferenceAdded
import org.javers.model.mapping.Property
import org.javers.model.object.graph.MultiEdge
import org.javers.model.object.graph.ObjectNode
import spock.lang.Specification

import static org.javers.test.EntityManagerFactory.createWithEntities
import static org.javers.test.assertion.ChangeAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import org.javers.model.object.graph.ObjectGraphBuilder

class MultiEdgeDifferenceCalculatorTest extends Specification {

    MultiEdgeDifferenceCalculator calculator = new MultiEdgeDifferenceCalculator()

    def entityManager

    def setup() {
        entityManager = createWithEntities(DummyUser)
    }

    def "should find added references to MultiEdge"() {
        given:
        def leftEdge = multiEdge([])
        def rightEdge = multiEdge([node("1"), node("2")])
        def ownerId = new GlobalCdoId("0101", entityManager.getByClass(DummyUser))

        when:
        def references = calculator.findAddedReferences(leftEdge, rightEdge, ownerId)

        then:
        references.size() == 2
        assertThat(references[0]).isReferenceAdded().hasCdoId("0101").hasEntityTypeOf(DummyUser)
        assertThat(references[1]).isReferenceAdded().hasCdoId("0101").hasEntityTypeOf(DummyUser)
    }

    def "should find skip previously existing references"() {
        given:
        def leftEdge = multiEdge([node("1")])
        def rightEdge = multiEdge([node("1"), node("2")])
        def ownerId = new GlobalCdoId("0010", entityManager.getByClass(DummyUser))

        when:
        def references = calculator.findAddedReferences(leftEdge, rightEdge, ownerId)

        then:
        references.size() == 1
        assertThat(references[0]).isReferenceAdded().hasCdoId("0010").hasEntityTypeOf(DummyUser)
        ((ReferenceAdded) references[0]).getAddedReference().getLocalCdoId() == "2"
    }

    def "should not find any added references"() {
        given:
        def leftEdge = multiEdge([node("1"), node("2")])
        def rightEdge = multiEdge([node("2")])
        def ownerId = new GlobalCdoId("0010", entityManager.getByClass(DummyUser))

        when:
        def references = calculator.findAddedReferences(leftEdge, rightEdge, ownerId)

        then:
        references == [] as Set
    }

    private MultiEdge multiEdge(references) {
        new MultiEdge([:] as Property).with { it.references = references; it }
    }

    private ObjectNode node(String id) {
        ObjectGraphBuilder objectGraphBuilder = new ObjectGraphBuilder(entityManager)
        objectGraphBuilder.buildGraph(dummyUser().withName(id).build())
    }
}
