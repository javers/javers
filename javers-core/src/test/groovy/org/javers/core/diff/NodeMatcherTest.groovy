package org.javers.core.diff

import org.javers.common.collections.Sets
import org.javers.model.object.graph.ObjectNode
import spock.lang.Specification

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
        List<NodePair> pairs = new NodeMatcher().match(previousGraph,currentGraph)

        then:
        pairs.size() == 1
        NodePair pair = pairs.get(0)
        pair.left.globalCdoId.localCdoId == "1"
        pair.right.globalCdoId.localCdoId == "1"
    }
}
