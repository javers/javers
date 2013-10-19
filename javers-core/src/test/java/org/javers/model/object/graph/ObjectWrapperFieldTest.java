package org.javers.model.object.graph;

import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.FieldBasedPropertyScanner;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class ObjectWrapperFieldTest extends ObjectWrapperTest {

    @BeforeMethod
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner (mapper);
        entityFactory = new EntityFactory(mapper, scanner);
    }
}
