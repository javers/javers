package org.javers.core.diff

import groovy.transform.TypeChecked
import org.javers.core.model.DummyUser
import org.javers.core.graph.ObjectNode
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author Maciej Zasada
 */
@TypeChecked
class DFSGraphToSetConverterTest extends AbstractDiffTest {


    def "should convert node with multi edge into set" () {
        given:
        DummyUser user = dummyUser().withDetailsList(2).build()
        ObjectNode graph = buildGraph(user)

        when:
        Set<ObjectNode> objectNodes = new DFSGraphToSetConverter().convertFromGraph(graph)

        then:
        objectNodes.size() == 3
    }

    def "should manage graph cycle" () {
        given:
        DummyUser emp =  dummyUser().withName("emp").build();
        DummyUser boss = dummyUser().withName("boss").withEmployee(emp).build();
        emp.setSupervisor(boss);
        ObjectNode graph = buildGraph(boss)

        when:
        Set<ObjectNode> objectNodes = new DFSGraphToSetConverter().convertFromGraph(graph)

        then:
        objectNodes.size() == 2
    }

    def "should convert node with singe edge into set" () {
        given:
        DummyUser user = dummyUser().withDetails().build()
        ObjectNode graph = buildGraph(user)

        when:
        Set<ObjectNode> objectNodes = new DFSGraphToSetConverter().convertFromGraph(graph)

        then:
        objectNodes.size() == 2
    }
}
