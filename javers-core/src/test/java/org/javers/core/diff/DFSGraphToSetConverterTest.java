package org.javers.core.diff;

import static org.javers.test.builder.DummyUserBuilder.dummyUser;
import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

import java.util.Set;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ValueObjectFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.object.graph.ObjectGraphBuilder;
import org.javers.model.object.graph.ObjectNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Maciej Zasada
 */
public class DFSGraphToSetConverterTest {

    private DFSGraphToSetConverter converter;
    private ObjectGraphBuilder objectGraphBuilder;

    @Before
    public void setUp() {
        converter = new DFSGraphToSetConverter();
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        EntityFactory entityFactory = new EntityFactory(scanner);
        ValueObjectFactory valueObjectFactory = new ValueObjectFactory();
        EntityManager entityManager = new EntityManager(entityFactory, valueObjectFactory, mapper);
        entityManager.registerEntity(DummyUser.class);
        entityManager.registerEntity(DummyUserDetails.class);
        entityManager.buildManagedClasses();
        objectGraphBuilder = new ObjectGraphBuilder(entityManager);
    }

    @Test
    public void shouldConvertNodeWithMultiEdgeIntoSet() {
        // given:
        DummyUser user = dummyUser().withName("1").withDetailsList(2).build();
        ObjectNode graph = objectGraphBuilder.buildGraph(user);

        // when:
        Set<ObjectNode> objectNodes = converter.convertFromGraph(graph);

        // then:
        Assert.assertEquals(3, objectNodes.size());
    }

    @Test
    public void shouldConvertNodeWithSingeEdgeIntoSet() {
        // given:
        DummyUser user = dummyUser().withName("1").withDetails(2L).build();
        ObjectNode graph = objectGraphBuilder.buildGraph(user);

        // when:
        Set<ObjectNode> objectNodes = converter.convertFromGraph(graph);

        // then:
        Assert.assertEquals(2, objectNodes.size());
    }
}
