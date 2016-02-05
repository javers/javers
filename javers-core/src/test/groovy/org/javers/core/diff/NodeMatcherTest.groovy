package org.javers.core.diff

import static org.javers.core.model.DummyUser.dummyUser

/**
 * @author bartosz walacik
 */
class NodeMatcherTest extends AbstractDiffTest{

    def "should match nodes with the same GlobalId"() {
        given:
        def previousGraph = buildLiveGraph(dummyUser("a").withDetails(2))

        def currentGraph =   buildLiveGraph(dummyUser("b").withDetails(2))

        when:
        def pairs = new NodeMatcher().match(new GraphPair(previousGraph,currentGraph))

        then:
        pairs.size() == 1
        RealNodePair pair = pairs[0]
        pair.left.globalId.cdoId == 2
        pair.right.globalId.cdoId == 2
    }
}
