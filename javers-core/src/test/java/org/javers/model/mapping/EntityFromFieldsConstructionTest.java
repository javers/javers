package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
@Test
public class EntityFromFieldsConstructionTest extends EntityConstructionTest {

    @BeforeMethod
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        entityFactory = new FieldBasedEntityFactory(typeMapper);
    }
}
