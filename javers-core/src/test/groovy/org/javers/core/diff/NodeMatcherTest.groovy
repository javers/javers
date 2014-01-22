package org.javers.core.diff

import org.javers.common.collections.Sets
import org.javers.model.object.graph.ObjectNode

import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class NodeMatcherTest extends AbstractDiffTest{

    def "should match nodes with the same GlobalCdoId"() {
        given:
        Set<ObjectNode> previousGraph = Sets.asSet(buildGraph(dummyUser().withName("1").build()),
                                                   buildGraph(dummyUser().withName("3").build()));
        Set<ObjectNode> currentGraph =  Sets.asSet(buildGraph(dummyUser().withName("1").build()),
                                                   buildGraph(dummyUser().withName("2").build()));

        when:
        List<RealNodePair> pairs = new NodeMatcher().match(previousGraph,currentGraph)

        then:
        pairs.size() == 1
        RealNodePair pair = pairs.get(0)
        pair.left.globalCdoId.cdoId == "1"
        pair.right.globalCdoId.cdoId == "1"
    }
}
