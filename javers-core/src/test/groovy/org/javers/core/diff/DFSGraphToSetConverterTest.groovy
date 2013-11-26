package org.javers.core.diff

import org.javers.core.model.DummyUser
import org.javers.model.object.graph.ObjectGraphBuilder;
import org.javers.model.object.graph.ObjectNode;
import org.junit.Assert
import org.junit.Test
import spock.lang.Specification;
import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author Maciej Zasada
 */
class DFSGraphToSetConverterTest extends Specification {

    ObjectGraphBuilder objectGraphBuilder(){
        javersTestAssembly().objectGraphBuilder
    }

    def "should convert node with multi edge into set" () {
        given:
        DummyUser user = dummyUser().withDetailsList(2).build()
        ObjectNode graph = objectGraphBuilder().buildGraph(user)

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
        ObjectNode graph = objectGraphBuilder().buildGraph(boss)

        when:
        Set<ObjectNode> objectNodes = new DFSGraphToSetConverter().convertFromGraph(graph)

        then:
        objectNodes.size() == 2
    }

    def "should convert node with singe edge into set" () {
        given:
        DummyUser user = dummyUser().withDetails().build()
        ObjectNode graph = objectGraphBuilder().buildGraph(user)

        when:
        Set<ObjectNode> objectNodes = new DFSGraphToSetConverter().convertFromGraph(graph)

        then:
        objectNodes.size() == 2
    }
}
