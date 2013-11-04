package org.javers.model.object.graph;


import org.javers.core.model.DummyUser;
import org.javers.model.mapping.EntityManager;
import org.javers.test.assertion.Assertions;
import org.junit.Test;

import static org.javers.test.assertion.NodeAssert.assertThat;
import static org.javers.test.builder.DummyUserBuilder.dummyUser;

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
        ObjectNode node = graphBuilder.build(user);

        //then
        Assertions.assertThat(node.getEntity().getSourceClass()).isSameAs(DummyUser.class);
        Assertions.assertThat(node.getCdoId()).isEqualTo("Mad Kaz") ;
        Assertions.assertThat(node.getEdges()).isEmpty();
    }

    @Test
    public void shouldBuildTwoNodesGraphForTheSameEntity(){
        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser user = dummyUser().withName("Mad Kaz").withSupervisor("Mad Stach").build();

        //when
        ObjectNode node = graphBuilder.build(user);

        //then
        assertThat(node).hasEdges(1)
                        .hasCdoWithId("Mad Kaz")
                        .andFirstEdge() //jump to EdgeAssert
                        .hasProperty("supervisor")
                        .isSingleEdge()
                        .refersToCdoWithId("Mad Stach");
    }

    @Test
    public void shouldBuildTwoNodesGraphForDifferentEntities() {
        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser user = dummyUser().withName("Mad Kaz").withDetails().build();

        //when
        ObjectNode node = graphBuilder.build(user);

        //then
        assertThat(node).hasEdges(1)
                        .hasCdoWithId("Mad Kaz")
                        .andFirstEdge() //jump to EdgeAssert
                        .hasProperty("dummyUserDetails")
                        .isSingleEdge()
                        .refersToCdoWithId(1L);
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
        ObjectNode node = graphBuilder.build(kaziki[0]);

        //then
        assertThat(node).hasEdges(1)
                        .hasCdoWithId("Mad Kaz 0")
                        .hasEdge("supervisor")
                        .isSingleEdge()
                        .refersToNodeWhich()
                .hasEdges(1)
                .hasCdoWithId("Mad Kaz 1")
                .hasEdge("supervisor")
                .isSingleEdge()
                .refersToNodeWhich()
                        .hasNoEdges()
                        .hasCdoWithId("Mad Kaz 2");
    }

    @Test
    public void shouldBuildFourNodesGraphWithThreeLevels() {
        // kaz - kas.details
        //    \
        //      stach - stach.details

        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser stach = dummyUser().withName("Mad Stach").withDetails(2L).build();
        DummyUser kaz   = dummyUser().withName("Mad Kaz").withDetails(1L).withSupervisor(stach).build();

        //when
        ObjectNode node = graphBuilder.build(kaz);

        //then
        assertThat(node).hasEdges(2)
                        .hasCdoWithId("Mad Kaz");

        assertThat(node).hasEdge("supervisor")
                        .isSingleEdge()
                        .refersToNodeWhich()
                        .hasCdoWithId("Mad Stach")
                        .hasEdge("dummyUserDetails")
                        .isSingleEdge()
                        .refersToCdoWithId(2L);

        assertThat(node).hasEdge("dummyUserDetails")
                        .isSingleEdge()
                        .refersToCdoWithId(1L);
    }

    @Test
    public void shouldBuildGraphWithOneSingleEdgeAndOneMultiEdge(){
        //given
        int numberOfDetailsInList = 3;
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser stach = dummyUser().withName("Mad Stach").withDetails(2L).withDetailsList(numberOfDetailsInList).build();

        //when
        ObjectNode node = graphBuilder.build(stach);

        //then
        assertThat(node).hasEdges(2).haveAtLeastSingleEdge(1).haveAtLeastMultiEdge(1);
    }

    @Test
    public void shouldBuildGraphWithThreeLevelsWithMultiEdge() {
        // kaz - kas.details
        //    \
        //      stach - stach.details
        //       \
        //        detailsList
        //         /   |   \
        //      id    id    id
        //given
        int numberOfDetailsInList = 3;
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser stach = dummyUser().withName("Mad Stach").withDetails(2L).withDetailsList(numberOfDetailsInList).build();
        DummyUser kaz   = dummyUser().withName("Mad Kaz").withDetails(1L).withSupervisor(stach).build();

        //when
        ObjectNode node = graphBuilder.build(kaz);

        //then
        assertThat(node).hasEdges(2)
                .hasCdoWithId("Mad Kaz");

        assertThat(node).hasEdge("supervisor")
                .isSingleEdge()
                .refersToNodeWhich()
                .hasCdoWithId("Mad Stach")
                .haveAtLeastMultiEdge(1)
                .hasEdge("dummyUserDetails")
                .isSingleEdge()
                .refersToCdoWithId(2L);

        assertThat(node).hasEdge("dummyUserDetails")
                .isSingleEdge()
                .refersToCdoWithId(1L);
    }

    public void shouldBuildGraphWithMultiEdgeContainMultiEdge(){
        // kaz
        //   \
        //     stach
        //    /  |  \
        //  Em1  Em2  rob
        //           /  |  \
        //          Em4 Em5 Em6
        //given
        int numberOfElements = 3;
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser rob = dummyUser().withName("Mad Rob").withEmployees(3).build();
        DummyUser stach = dummyUser().withName("Mad Stach")
                .withDetails(2L)
                .withDetailsList(numberOfElements)
                .withEmployees(2)
                .build();
        stach.getEmployeesList().add(rob);
        DummyUser kaz   = dummyUser().withName("Mad Kaz").withDetails(1L).withSupervisor(stach).build();

        //when
        ObjectNode node = graphBuilder.build(kaz);

        //then
        assertThat(node).hasEdges(2)
                .hasCdoWithId("Mad Kaz");

        assertThat(node).hasEdge("supervisor")
                .isSingleEdge()
                .refersToNodeWhich()
                .hasCdoWithId("Mad Stach")
                .haveAtLeastMultiEdge(1)
                .hasEdge("employeesList")
                .isMultiEdge();
    }
}
