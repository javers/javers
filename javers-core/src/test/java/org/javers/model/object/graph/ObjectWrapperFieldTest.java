package org.javers.model.object.graph;

import org.javers.model.mapping.FieldBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

/**
 * @author bartosz walacik
 */
public class ObjectWrapperFieldTest extends ObjectWrapperTest {

    @BeforeMethod
    public void setUp() {
        entityFactory = new FieldBasedEntityFactory(new TypeMapper());
    }
}
