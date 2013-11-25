package org.javers.core.diff.appenders;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ValueObjectFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.object.graph.ObjectGraphBuilder;
import org.javers.model.object.graph.ObjectNode;
import org.javers.test.builder.DummyUserBuilder;
import org.junit.Before;

/**
 * @author Maciej Zasada
 */
public abstract class ChangeSetAppenderTest {
    protected  EntityManager entityManager;

    @Before
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        EntityFactory entityFactory = new EntityFactory(scanner);
        ValueObjectFactory valueObjectFactory = new ValueObjectFactory(scanner);
        entityManager = new EntityManager(entityFactory, valueObjectFactory, mapper);
        entityManager.registerEntity(DummyUser.class);
        entityManager.registerEntity(DummyUserDetails.class);
        entityManager.buildManagedClasses();

    }

    protected ObjectNode createObjectNodeWithId(String id) {
        ObjectGraphBuilder objectGraphBuilder = new ObjectGraphBuilder(entityManager);
        return objectGraphBuilder.buildGraph(DummyUserBuilder.dummyUser().withName(id).build());
    }
}
