package org.javers.core.diff.appenders;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.object.graph.ObjectGraphBuilder;
import org.javers.model.object.graph.ObjectNode;
import org.javers.test.builder.DummyUserBuilder;
import org.testng.annotations.BeforeMethod;

/**
 * @author Maciej Zasada
 */
public class ChangeSetAppenderTest {
    protected ObjectGraphBuilder objectGraphBuilder;

    @BeforeMethod
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        EntityManager entityManager = new EntityManager(new EntityFactory(mapper, scanner));
        entityManager.registerEntity(DummyUser.class);
        entityManager.registerEntity(DummyUserDetails.class);
        entityManager.buildManagedClasses();
        objectGraphBuilder = new ObjectGraphBuilder(entityManager);
    }

    protected ObjectNode createObjectNodeWithId(String id) {
        return objectGraphBuilder.build(DummyUserBuilder.dummyUser().withName(id).build());
    }
}
