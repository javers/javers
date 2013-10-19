package org.javers.common.reflection.org.javers.model.mapping;

import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.FieldBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class EntityIdFromFieldTest extends EntityIdTest {

    @Before
    public void setUp() {
        TypeMapper mapper = typeMapper().registerValueObject(DummyUserDetails.class).build();
        entityFactory = new FieldBasedEntityFactory(mapper);
    }
}
