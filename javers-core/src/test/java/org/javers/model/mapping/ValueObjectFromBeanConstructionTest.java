package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author pawel szymczyk
 */
public class ValueObjectFromBeanConstructionTest extends ValueObjectConstructionTest{

    @Before
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(typeMapper);
        factory = new ValueObjectFactory(scanner);
    }
}
