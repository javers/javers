package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author bartosz walacik
 */
@Test
public class EntityFromFieldsConstructionTest extends EntityConstructionTest {


    @BeforeMethod
    public void setUp() {
        entityFactory = new FieldBasedEntityFactory(new TypeMapper());
    }

}
