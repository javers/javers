package org.javers.model.object.graph;

import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class ObjectWrapperBeanTest extends ObjectWrapperTest {

    @Before
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        entityFactory = new BeanBasedEntityFactory(mapper);
    }
}
