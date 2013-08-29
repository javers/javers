package org.javers.model.mapping;

import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyUser;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

/**
 * @author bartosz walacik
 */
public class EntityIdFromBeanTest extends EntityIdTest {

    @BeforeMethod
    public void setUp() {

        TypeMapper mapper = new TypeMapper();
        mapper.registerObjectValueType(DummyAddress.class);
        entityFactory = new BeanBasedEntityFactory(mapper);
    }
}
