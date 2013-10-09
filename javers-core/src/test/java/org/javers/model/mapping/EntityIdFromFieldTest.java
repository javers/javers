package org.javers.model.mapping;

import org.javers.core.model.DummyNetworkAddress;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class EntityIdFromFieldTest extends EntityIdTest {
    @BeforeMethod
    public void setUp() {
        TypeMapper mapper = typeMapper()
                                .registerEntity(DummyUserDetails.class)
                                .registerValueObject(DummyNetworkAddress.class)
                                .build();
        entityFactory = new FieldBasedEntityFactory(mapper);
    }
}
