package org.javers.model.object.graph

import org.javers.core.metamodel.property.EntityManager
import org.javers.core.metamodel.property.ManagedClassFactory
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.model.mapping.type.TypeMapper
import spock.lang.Specification

import static org.javers.test.assertion.NodeAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails

/**
 * @author bartosz walacik
 */
abstract class ObjectGraphBuilderTest extends Specification {

    protected TypeMapper mapper = new TypeMapper()
    protected EntityManager entityManager

    protected void buildEntityManager(ManagedClassFactory ef) {

        //will be refactored ASAP
        mapper.registerEntityReferenceType(DummyUser)
        mapper.registerEntityReferenceType(DummyUserDetails)
        mapper.registerEntityReferenceType(DummyAddress)
        mapper.registerEntityReferenceType(DummyNetworkAddress)

        //will be refactored ASAP
        entityManager = new EntityManager(ef)
        entityManager.registerEntity(DummyUser)
        entityManager.registerEntity(DummyUserDetails)
        entityManager.registerValueObject(DummyAddress)
        entityManager.registerValueObject(DummyNetworkAddress)
        entityManager.buildManagedClasses()
    }


    def "should build one node graph from Entity"(){
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUser user = dummyUser().withName("Mad Kaz").build()

        when:
        ObjectNode node = graphBuilder.buildGraph(user)

        then:
        assertThat(node).hasNoEdges()
                .hasCdo(user)
                .hasInstanceId(DummyUser, "Mad Kaz")
    }


    def "should build graph starting from root ValueObject"(){
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyAddress address = new DummyAddress("any","any")

        when:
        ObjectNode node = graphBuilder.buildGraph(address)

        then:
        assertThat(node).hasNoEdges()
                .hasCdo(address)
                .hasUnboundedValueObjectId(DummyAddress)
    }


    def "should build graph with ValueObject node"() {
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUserDetails user = dummyUserDetails(1).withAddress().build()

        when:
        ObjectNode node = graphBuilder.buildGraph(user)

        then:
        assertThat(node).hasCdo(user)
                .hasSingleEdge("dummyAddress")
                .andTargetNode()
                .hasNoEdges()
                .hasCdo(user.getDummyAddress())
                .hasValueObjectId(DummyAddress, user, "dummyAddress")
    }


    def "should build two node graph for the same Entity"(){
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUser user = dummyUser().withName("Mad Kaz").withSupervisor("Mad Stach").build()

        when:
        ObjectNode node = graphBuilder.buildGraph(user)

        then:
        assertThat(node).hasEdges(1)
                .hasCdoId("Mad Kaz")
                .hasEdge("supervisor") //jump to EdgeAssert
                .isSingleEdgeTo("Mad Stach")
    }


    def "should build two node graph for different Entities"() {
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUser user = dummyUser().withName("Mad Kaz").withDetails().build()

        when:
        ObjectNode node = graphBuilder.buildGraph(user)

        then:
        assertThat(node).hasEdges(1)
                .hasCdoId("Mad Kaz")
                .hasEdge("dummyUserDetails")//jump to EdgeAssert
                .isSingleEdgeTo(1L)
    }



    def "shouldBuildThreeNodesLinearGraph"() {
        given:
        //kaz0 - kaz1 - kaz2
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper);
        DummyUser[] kaziki = new DummyUser[4];
        for (int i=0; i<3; i++){
            kaziki[i] = dummyUser().withName("Mad Kaz "+i).build();
            if (i>0) {
                kaziki[i-1].setSupervisor(kaziki[i]);
            }
        }

        when:
        ObjectNode node = graphBuilder.buildGraph(kaziki[0]);

        then:
        assertThat(node).hasEdges(1)
                .hasCdoId("Mad Kaz 0")
                .hasSingleEdge("supervisor")
                .andTargetNode()
                .hasEdges(1)
                .hasCdoId("Mad Kaz 1")
                .hasSingleEdge("supervisor")
                .andTargetNode()
                .hasNoEdges()
                .hasCdoId("Mad Kaz 2");
    }


    def "should build four node graph with three levels"() {
        // kaz - kaz.details
        //    \
        //      stach - stach.details
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUser stach = dummyUser().withName("Mad Stach").withDetails(2L).build()
        DummyUser kaz   = dummyUser().withName("Mad Kaz").withDetails(1L).withSupervisor(stach).build()

        when:
        ObjectNode node = graphBuilder.buildGraph(kaz)

        then:
        assertThat(node).hasEdges(2)
                .hasCdoId("Mad Kaz")
                .and().hasEdge("supervisor")
                .isSingleEdge()
                .andTargetNode()
                .hasCdoId("Mad Stach")
                .hasEdge("dummyUserDetails")
                .isSingleEdgeTo(2L)

        assertThat(node).hasEdge("dummyUserDetails")
                .isSingleEdgeTo(1L)
    }


    def "should build graph with one SingleEdge and one MultiEdge"(){
        //       stach - details
        //       \
        //        detailsList
        //         /   |   \
        //      id    id    id
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUser stach = dummyUser().withName("Mad Stach").withDetails(2L).withDetailsList(3).build()

        when:
        ObjectNode node = graphBuilder.buildGraph(stach)

        then:
        assertThat(node).hasEdges(2)
        assertThat(node).hasSingleEdge("dummyUserDetails")
        assertThat(node).hasMultiEdge("dummyUserDetailsList").ofSize(3)
    }


    def "should build graph with three levels with MultiEdge"() {
        // kaz
        //    \
        //      stach
        //       \
        //        detailsList
        //         /   |   \
        //      id    id    id
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUser stach = dummyUser().withName("Stach").withDetailsList(3).build()
        DummyUser kaz   = dummyUser().withName("Mad Kaz").withSupervisor(stach).build()

        when:
        ObjectNode node = graphBuilder.buildGraph(kaz)

        then:
        assertThat(node).hasCdoId("Mad Kaz")
                .hasEdge("supervisor")
                .isSingleEdgeTo("Stach")
                .andTargetNode()
                .hasMultiEdge("dummyUserDetailsList").ofSize(3)
    }


    def "should build graph with MultiEdge to node with MultiEdge"(){
        //              kaz
        //                 \
        //                 stach
        //              /    |    \
        //             rob   Em1  Em2
        //           /  |  \
        //          Em1 Em2 Em3
        given:
        int numberOfElements = 3
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUser rob = dummyUser().withName("rob").withEmployees(3).build()
        DummyUser stach = dummyUser().withName("stach")
                .withEmployee(rob)
                .withEmployees(2)
                .build()
        DummyUser kaz   = dummyUser().withName("kaz").withSupervisor(stach).build()

        when:
        ObjectNode node = graphBuilder.buildGraph(kaz)

        then:
        assertThat(node).hasCdoId("kaz")
                .and().hasEdge("supervisor")
                .isSingleEdgeTo("stach")
                .andTargetNode()
                .hasEdge("employeesList")
                .isMultiEdge("Em1","Em2","rob")
                .andTargetNode("rob")
                .hasEdge("employeesList")
                .isMultiEdge("Em1", "Em2", "Em3")
    }


    def "should manage graph cycles"(){
        //superKaz
        //  \ \   \
        //   kaz   \
        //    \     \
        //      microKaz
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)

        DummyUser superKaz = dummyUser().withName("superKaz").build()
        DummyUser kaz   =    dummyUser().withName("kaz").withSupervisor(superKaz).build()
        DummyUser microKaz = dummyUser().withName("microKaz").withSupervisor(kaz).build()
        superKaz.setEmployeesList(kaz, microKaz)

        when:
        ObjectNode node = graphBuilder.buildGraph(superKaz)

        then:

        //small cycle
        assertThat(node).hasCdoId("superKaz")
                .hasEdge("employeesList")
                .isMultiEdge("kaz", "microKaz")
                .andTargetNode("kaz")
                .hasEdge("supervisor")
                .isSingleEdgeTo("superKaz")

        //large cycle
        assertThat(node).hasCdoId("superKaz")
                .hasMultiEdge("employeesList")
                .andTargetNode("microKaz")
                .hasEdge("supervisor")
                .isSingleEdgeTo("kaz")
                .andTargetNode()
                .hasEdge("supervisor")
                .isSingleEdgeTo("superKaz")
    }


    def "should build graph with primitive types Set"() {
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUser dummyUser = dummyUser().withName("name").withStringsSet("1", "2", "3").build()

        when:
        ObjectNode node = graphBuilder.buildGraph(dummyUser)

        then:
        assertThat(node).hasNoEdges()
    }


    def "should build graph with primitive types List"(){
        given:
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager, mapper)
        DummyUser dummyUser = dummyUser().withName("name").withIntegerList(1, 2, 3, 4).build()

        when:
        ObjectNode node = graphBuilder.buildGraph(dummyUser)

        then:
        assertThat(node).hasNoEdges()
    }
}
