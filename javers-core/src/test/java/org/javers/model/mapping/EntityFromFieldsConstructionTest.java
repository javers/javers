package org.javers.model.mapping;

import org.javers.core.model.DummyAddress;
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
        TypeMapper mapper = new TypeMapper();
        mapper.registerObjectValueType(DummyAddress.class);
        entityFactory = new FieldBasedEntityFactory(mapper);
    }

}
