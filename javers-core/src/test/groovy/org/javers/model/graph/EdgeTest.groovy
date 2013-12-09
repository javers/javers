package org.javers.model.graph

import org.javers.core.JaversTestBuilder
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.model.domain.GlobalCdoId
import org.javers.model.object.graph.Direction
import org.javers.model.object.graph.MultiEdge
import org.javers.model.object.graph.ObjectNode
import org.javers.model.object.graph.SingleEdge
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.test.builder.DummyUserBuilder.dummyUser


class EdgeTest extends Specification{

    @Shared JaversTestBuilder javersTestBuilder = javersTestAssembly()

    def "should contain out entity out single edge"() {
        given:
        DummyUser user = dummyUser().withName("Mad Kaz").withDetails().build()
        ObjectNode node = javersTestBuilder.createObjectGraphBuilder().buildGraph(user)

        when:
        GlobalCdoId cdoId = ((SingleEdge) node.edges[0]).getReferencedGlobalCdoId(Direction.OUT)

        then:
        cdoId == new GlobalCdoId(user.name, javersTestBuilder.entityManager.getByClass(DummyUser))
    }

    def "should contain out entity in single edge"() {
        given:
        DummyUser user = dummyUser().withName("Mad Kaz").withDetails().build()
        ObjectNode node = javersTestBuilder.createObjectGraphBuilder().buildGraph(user)

        when:
        GlobalCdoId cdoId = ((SingleEdge) node.edges[0]).getReferencedGlobalCdoId(Direction.IN)

        then:
        cdoId == new GlobalCdoId(user.dummyUserDetails.id, javersTestBuilder.entityManager.getByClass(DummyUserDetails))
    }

    def "should contain out references in one to many multiple edge"() {
        given:
        DummyUser user = dummyUser().withName("Mad Stach").withDetailsList(3).build();
        ObjectNode node = javersTestBuilder.createObjectGraphBuilder().buildGraph(user)

        when:
        List<GlobalCdoId> ids = ((MultiEdge) node.edges[0]).getReferencedGlobalCdoIds(Direction.OUT)

        then:
        ids.size() == 1
    }

    def "should contain in references in one to many multiple edge"() {
        given:
        DummyUser user = dummyUser().withName("Mad Stach").withDetailsList(3).build();
        ObjectNode node = javersTestBuilder.createObjectGraphBuilder().buildGraph(user)

        when:
        List<GlobalCdoId> ids = ((MultiEdge) node.edges[0]).getReferencedGlobalCdoIds(Direction.IN)

        then:
        ids.size() == 3
    }
}
