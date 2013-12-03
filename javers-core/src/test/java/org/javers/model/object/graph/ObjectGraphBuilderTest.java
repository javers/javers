package org.javers.model.object.graph;


import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyUser;
import org.javers.model.mapping.EntityManager;
import org.junit.Test;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionBdd.when;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.javers.test.assertion.NodeAssert.assertThat;
import static org.javers.test.builder.DummyUserBuilder.dummyUser;
import static org.javers.test.assertion.JaversExceptionAssert.assertThat;

/**
 * @author bartosz walacik
 */
public abstract class ObjectGraphBuilderTest {

    protected EntityManager entityManager;

    @Test
    public void shouldBuildOneNodeGraph(){
        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser user = dummyUser().withName("Mad Kaz").build();

        //when
        ObjectNode node = graphBuilder.buildGraph(user);

        //then
        assertThat(node.getEntity().getSourceClass()).isSameAs(DummyUser.class);
        assertThat(node.getLocalCdoId()).isEqualTo("Mad Kaz") ;
        assertThat(node.getEdges()).isEmpty();
    }

    @Test
    public void shouldBuildTwoNodesGraphForTheSameEntity(){
        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser user = dummyUser().withName("Mad Kaz").withSupervisor("Mad Stach").build();

        //when
        ObjectNode node = graphBuilder.buildGraph(user);

        //then
        assertThat(node).hasEdges(1)
                        .hasCdoId("Mad Kaz")
                        .hasEdge("supervisor") //jump to EdgeAssert
                        .isSingleEdgeTo("Mad Stach");
    }

    @Test
    public void shouldBuildTwoNodesGraphForDifferentEntities() {
        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser user = dummyUser().withName("Mad Kaz").withDetails().build();

        //when
        ObjectNode node = graphBuilder.buildGraph(user);

        //then
        assertThat(node).hasEdges(1)
                        .hasCdoId("Mad Kaz")
                        .hasEdge("dummyUserDetails")//jump to EdgeAssert
                        .isSingleEdgeTo(1L);
    }

    @Test
    public void shouldBuildThreeNodesLinearGraph() {
        //kaz0 - kaz1 - kaz2
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser[] kaziki = new DummyUser[4];
        for (int i=0; i<3; i++){
            kaziki[i] = dummyUser().withName("Mad Kaz "+i).build();
            if (i>0) {
                kaziki[i-1].setSupervisor(kaziki[i]);
            }
        }

        //when
        ObjectNode node = graphBuilder.buildGraph(kaziki[0]);

        //then
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

    @Test
    public void shouldBuildFourNodesGraphWithThreeLevels() {
        // kaz - kaz.details
        //    \
        //      stach - stach.details

        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser stach = dummyUser().withName("Mad Stach").withDetails(2L).build();
        DummyUser kaz   = dummyUser().withName("Mad Kaz").withDetails(1L).withSupervisor(stach).build();

        //when
        ObjectNode node = graphBuilder.buildGraph(kaz);

        //then
        assertThat(node).hasEdges(2)
                        .hasCdoId("Mad Kaz")
                  .and().hasEdge("supervisor")
                        .isSingleEdge()
                        .andTargetNode()
                        .hasCdoId("Mad Stach")
                        .hasEdge("dummyUserDetails")
                        .isSingleEdgeTo(2L);

        assertThat(node).hasEdge("dummyUserDetails")
                        .isSingleEdgeTo(1L);
    }

    @Test
    public void shouldBuildGraphWithOneSingleEdgeAndOneMultiEdge(){
        //       stach - details
        //       \
        //        detailsList
        //         /   |   \
        //      id    id    id

        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser stach = dummyUser().withName("Mad Stach").withDetails(2L).withDetailsList(3).build();

        //when
        ObjectNode node = graphBuilder.buildGraph(stach);

        //then
        assertThat(node).hasEdges(2);
        assertThat(node).hasSingleEdge("dummyUserDetails");
        assertThat(node).hasMultiEdge("dummyUserDetailsList").ofSize(3);
    }

    @Test
    public void shouldBuildGraphWithThreeLevelsWithMultiEdge() {
        // kaz
        //    \
        //      stach
        //       \
        //        detailsList
        //         /   |   \
        //      id    id    id

        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser stach = dummyUser().withName("Stach").withDetailsList(3).build();
        DummyUser kaz   = dummyUser().withName("Mad Kaz").withSupervisor(stach).build();

        //when
        ObjectNode node = graphBuilder.buildGraph(kaz);

        //then
        assertThat(node).hasCdoId("Mad Kaz")
                        .hasEdge("supervisor")
                        .isSingleEdgeTo("Stach")
                        .andTargetNode()
                        .hasMultiEdge("dummyUserDetailsList").ofSize(3);
    }

    @Test
    public void shouldBuildGraphWithMultiEdgeToNodeWithMultiEdge(){
        //              kaz
        //                 \
        //                 stach
        //              /    |    \
        //             rob   Em1  Em2
        //           /  |  \
        //          Em1 Em2 Em3
        //given
        int numberOfElements = 3;
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser rob = dummyUser().withName("rob").withEmployees(3).build();
        DummyUser stach = dummyUser().withName("stach")
                                     .withEmployee(rob)
                                     .withEmployees(2)
                                     .build();
        DummyUser kaz   = dummyUser().withName("kaz").withSupervisor(stach).build();

        //when
        ObjectNode node = graphBuilder.buildGraph(kaz);

        //then
        assertThat(node).hasCdoId("kaz")
                  .and().hasEdge("supervisor")
                        .isSingleEdgeTo("stach")
                        .andTargetNode()
                        .hasEdge("employeesList")
                        .isMultiEdge("Em1","Em2","rob")
                        .andTargetNode("rob")
                        .hasEdge("employeesList")
                        .isMultiEdge("Em1", "Em2", "Em3");
    }

    @Test
    public void shouldThrowExceptionWhenTryToBuildGraphFromValueObject() throws Throwable {
        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyAddress valueObject = new DummyAddress();

        when(graphBuilder).buildGraph(valueObject);

        //then
        assertThat((JaversException) caughtException())
                .hasCode(JaversExceptionCode.UNEXPECTED_VALUE_OBJECT);
    }

    @Test
    public void shouldManageGraphCycles(){
        //superKaz
        //  \ \   \
        //   kaz   \
        //    \     \
        //      microKaz

        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);

        DummyUser superKaz = dummyUser().withName("superKaz").build();
        DummyUser kaz   =    dummyUser().withName("kaz").withSupervisor(superKaz).build();
        DummyUser microKaz = dummyUser().withName("microKaz").withSupervisor(kaz).build();
        superKaz.setEmployeesList(kaz, microKaz);

        //when
        ObjectNode node = graphBuilder.buildGraph(superKaz);

        //then

        //small cycle
        assertThat(node).hasCdoId("superKaz")
                        .hasEdge("employeesList")
                        .isMultiEdge("kaz", "microKaz")
                        .andTargetNode("kaz")
                        .hasEdge("supervisor")
                        .isSingleEdgeTo("superKaz");

        //large cycle
        assertThat(node).hasCdoId("superKaz")
                        .hasMultiEdge("employeesList")
                        .andTargetNode("microKaz")
                        .hasEdge("supervisor")
                        .isSingleEdgeTo("kaz")
                        .andTargetNode()
                        .hasEdge("supervisor")
                        .isSingleEdgeTo("superKaz");
    }

    @Test
    public void shouldBuildGraphWithPrimitiveTypesSet() throws Throwable {
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser dummyUser = dummyUser().withName("name").withStringsSet("1", "2", "3").build();

        //throw exception
        ObjectNode node = graphBuilder.buildGraph(dummyUser);
    }

    @Test
    public void shouldBuildGraphWithPrimitiveTypesList() throws Throwable {
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser dummyUser = dummyUser().withName("name").withIntegerList(1, 2, 3, 4).build();

        //throw exception
        ObjectNode node = graphBuilder.buildGraph(dummyUser);
    }
}
