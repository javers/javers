package org.javers.model.object.graph;


import org.javers.core.model.DummyUser;
import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
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
        entityManager = new EntityManager( new BeanBasedEntityFactory(new TypeMapper()));
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
}
