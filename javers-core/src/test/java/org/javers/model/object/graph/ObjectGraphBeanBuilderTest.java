package org.javers.model.object.graph;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ValueObjectFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author Pawel Cierpiatka
 */
public class ObjectGraphBeanBuilderTest extends ObjectGraphBuilderTest {

    @Before
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        EntityFactory entityFactory = new EntityFactory(scanner);
        ValueObjectFactory valueObjectFactory = new ValueObjectFactory();
        entityManager = new EntityManager(entityFactory, valueObjectFactory, mapper);
        entityManager.registerEntity(DummyUser.class);
        entityManager.registerEntity(DummyUserDetails.class);
        entityManager.buildManagedClasses();
    }



}
