package org.javers.core.diff

import org.javers.common.collections.Sets
import org.javers.core.model.DummyUser
import org.javers.model.object.graph.Fake
import org.javers.model.object.graph.ObjectNode
import org.javers.model.object.graph.ObjectWrapper

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
        pairs.size() == 2
        with(pairs.get(0)) {
            left.globalCdoId.cdoId == "1"
            right.globalCdoId.cdoId == "1"
        }

        with(pairs.get(1)) {
            left.class == Fake
            right.class == ObjectWrapper
            left.globalCdoId.cdoId == right.globalCdoId.cdoId
        }
    }
}
