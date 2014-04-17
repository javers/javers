package org.javers.core.diff

import org.javers.core.graph.ObjectNode

import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class NodeMatcherTest extends AbstractDiffTest{

    def "should match nodes with the same GlobalCdoId"() {
        given:
        def previousGraph = buildLiveGraph(dummyUser().withName("a").withDetails(2).build());

        def currentGraph =   buildLiveGraph(dummyUser().withName("b").withDetails(2).build());

        when:
        List<RealNodePair> pairs = new NodeMatcher().match(new GraphPair(previousGraph,currentGraph))

        then:
        pairs.size() == 1
        RealNodePair pair = pairs.get(0)
        pair.left.globalCdoId.cdoId == 2
        pair.right.globalCdoId.cdoId == 2
    }
}
