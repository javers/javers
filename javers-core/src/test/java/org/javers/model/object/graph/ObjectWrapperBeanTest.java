package org.javers.model.object.graph;

import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

/**
 * @author bartosz walacik
 */
public class ObjectWrapperBeanTest extends ObjectWrapperTest {

    @BeforeMethod
    public void setUp() {
        entityFactory = new BeanBasedEntityFactory(new TypeMapper());
    }
}
