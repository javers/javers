package org.javers.model.graph

import org.javers.core.JaversTestBuilder
import org.javers.core.model.DummyUser
import org.javers.model.domain.GlobalCdoId
import org.javers.model.object.graph.MultiEdge
import org.javers.model.object.graph.ObjectNode
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.test.builder.DummyUserBuilder.dummyUser


class EdgeTest extends Specification{

    @Shared JaversTestBuilder javersTestBuilder = javersTestAssembly()

    def "should contain references in one to many multiple edge"() {
        given:
        DummyUser user = dummyUser().withName("Mad Stach").withDetailsList(3).build();
        ObjectNode node = javersTestBuilder.createObjectGraphBuilder().buildGraph(user)

        when:
        List<GlobalCdoId> ids = ((MultiEdge) node.edges[0]).getReferencedGlobalCdoIds()

        then:
        ids.size() == 3
    }
}
