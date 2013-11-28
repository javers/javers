package org.javers.core.diff

import org.javers.common.collections.Sets
import org.javers.core.diff.appenders.NewObjectAppender
import org.javers.core.model.DummyUser
import org.javers.model.domain.Diff
import org.javers.model.object.graph.ObjectNode

import static org.javers.test.assertion.DiffAssert.assertThat
import static org.javers.test.assertion.DiffAssert.assertThat

/**
 * @author bartosz walacik
 */
class NodeMatcherTest extends AbstractDiffTest{

    def "should match nodes with the same GlobalCdoId"() {
        given:
        Set<ObjectNode> previousGraph = Sets.asSet(buildDummyUserNode("1"),
                                                   buildDummyUserNode("3"));
        Set<ObjectNode> currentGraph =  Sets.asSet(buildDummyUserNode("1"),
                                                   buildDummyUserNode("2"));

        when:
        List<NodePair> pairs = new NodeMatcher().match(previousGraph,currentGraph)

        then:
        pairs.size() == 1
        NodePair pair = pairs.get(0)
        pair.left.globalCdoId.localCdoId == "1"
        pair.right.globalCdoId.localCdoId == "1"
    }
}
