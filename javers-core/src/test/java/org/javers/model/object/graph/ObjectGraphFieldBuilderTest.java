package org.javers.model.object.graph;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.FieldBasedPropertyScanner;
import org.javers.model.mapping.ValueObjectFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class ObjectGraphFieldBuilderTest extends ObjectGraphBuilderTest {

    @Before
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner(mapper);
        EntityFactory ef = new EntityFactory(scanner);
        ValueObjectFactory vf = new ValueObjectFactory();
        entityManager = new EntityManager(ef, vf, mapper);
        entityManager.registerEntity(DummyUser.class);
        entityManager.registerEntity(DummyUserDetails.class);
        entityManager.buildManagedClasses();
    }

}
