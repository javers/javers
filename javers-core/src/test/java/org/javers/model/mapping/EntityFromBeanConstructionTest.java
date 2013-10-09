package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class EntityFromBeanConstructionTest extends EntityConstructionTest {

    @BeforeMethod
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        entityFactory = new BeanBasedEntityFactory(typeMapper);
    }
}
