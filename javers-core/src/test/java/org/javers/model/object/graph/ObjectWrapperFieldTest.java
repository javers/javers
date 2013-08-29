package org.javers.model.object.graph;

import org.javers.model.mapping.FieldBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class ObjectWrapperFieldTest extends ObjectWrapperTest {

    @BeforeMethod
    public void setUp() {
        TypeMapper mapper = typeMapper().withAllDummyModels().build();
        entityFactory = new FieldBasedEntityFactory(mapper);
    }
}
