package org.javers.model.object.graph;


import org.javers.core.model.DummyUser;
import org.javers.model.mapping.*;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.test.assertion.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author bartosz walacik
 */
@Test
public class ObjectGraphBuilderTest {

    protected EntityManager entityManager;

    @BeforeMethod
    public void setUp() {
        TypeMapper mapper = new TypeMapper();
        entityManager = new EntityManager(new BeanBasedEntityFactory(mapper));

        entityManager.manage(DummyUser.class);
    }

    @Test
    public void shouldBuildOneNodeGraph(){
        //given
        ObjectGraphBuilder graphBuilder = new ObjectGraphBuilder(entityManager);
        DummyUser user = new DummyUser("Mad Kaz");

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
        DummyUser user = new DummyUser("Mad Kaz");
        DummyUser superUser = new DummyUser("Mad Stach");
        user.setSupervisor(superUser);

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
