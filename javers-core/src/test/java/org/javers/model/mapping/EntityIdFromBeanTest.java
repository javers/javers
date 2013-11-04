package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class EntityIdFromBeanTest extends EntityIdTest {

    @BeforeMethod
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        entityFactory = new EntityFactory(scanner);
    }
}
