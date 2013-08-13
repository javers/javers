package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

/**
 * @author bartosz walacik
 */
public class EntityIdFromBeanTest extends EntityIdTest {
    @BeforeMethod
    public void setUp() {
        entityFactory = new BeanBasedEntityFactory(new TypeMapper());
    }
}
