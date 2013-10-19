package org.javers.common.reflection.org.javers.model.mapping;

import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class EntityIdFromBeanTest extends EntityIdTest {

    @Before
    public void setUp() {

        TypeMapper mapper = typeMapper().registerValueObject(DummyUserDetails.class).build();
        entityFactory = new BeanBasedEntityFactory(mapper);
    }
}
