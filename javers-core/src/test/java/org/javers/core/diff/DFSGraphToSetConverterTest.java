package org.javers.core.diff;

import static org.javers.test.builder.DummyUserBuilder.dummyUser;
import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

import java.util.Set;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.object.graph.ObjectGraphBuilder;
import org.javers.model.object.graph.ObjectNode;
import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Maciej Zasada
 */
@Test
public class DFSGraphToSetConverterTest {

    private DFSGraphToSetConverter converter;
    private ObjectGraphBuilder objectGraphBuilder;

    @BeforeMethod
    public void setUp() {
        converter = new DFSGraphToSetConverter();
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        EntityManager entityManager = new EntityManager(new EntityFactory(mapper, scanner));
        entityManager.registerEntity(DummyUser.class);
        entityManager.registerEntity(DummyUserDetails.class);
        entityManager.buildManagedClasses();
        objectGraphBuilder = new ObjectGraphBuilder(entityManager);
    }

    public void shouldConvertNodeWithMultiEdgeIntoSet() {
        // given:
        DummyUser user = dummyUser().withName("1").withDetailsList(2).build();
        ObjectNode graph = objectGraphBuilder.build(user);

        // when:
        Set<ObjectNode> objectNodes = converter.convertFromGraph(graph);

        // then:
        Assert.assertEquals(3, objectNodes.size());
    }

    public void shouldConvertNodeWithSingeEdgeIntoSet() {
        // given:
        DummyUser user = dummyUser().withName("1").withDetails(2L).build();
        ObjectNode graph = objectGraphBuilder.build(user);

        // when:
        Set<ObjectNode> objectNodes = converter.convertFromGraph(graph);

        // then:
        Assert.assertEquals(2, objectNodes.size());
    }
}
