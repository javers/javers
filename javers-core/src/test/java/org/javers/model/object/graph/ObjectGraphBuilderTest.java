package org.javers.model.object.graph;


import org.javers.core.model.DummyUser;
import org.javers.model.mapping.EntityManager;
import org.javers.test.assertion.Assertions;
import org.javers.test.assertion.EdgeAssert;
import org.javers.test.assertion.NodeAssert;
import org.testng.annotations.Test;

import static org.javers.test.builder.DummyUserBuilder.dummyUser;

/**
 * @author bartosz walacik
 */
@Test
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
    public void shouldBuildTwoNodesGraph(){
        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser user = dummyUser().withName("Mad Kaz").withSupervisor("Mad Stach").build();

        //when
        ObjectNode node = graphBuilder.build(user);

        //then
        NodeAssert.assertThat(node).hasEdges(1)
                .hasCdoWithId("Mad Kaz");

        Edge edge = node.getEdges().get(0);
        EdgeAssert.assertThat(edge).hasProperty("supervisor")
                .isSingleEdge()
                .refersToCdoWithId("Mad Stach");
    }
}
