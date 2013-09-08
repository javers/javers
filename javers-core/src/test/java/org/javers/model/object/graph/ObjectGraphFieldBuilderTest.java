package org.javers.model.object.graph;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class ObjectGraphFieldBuilderTest extends ObjectGraphBuilderTest {

    @BeforeMethod
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        entityManager = new EntityManager(new BeanBasedEntityFactory(mapper));
        entityManager.registerEntity(DummyUser.class);
        entityManager.registerEntity(DummyUserDetails.class);
        entityManager.buildManagedClasses();
    }

}
