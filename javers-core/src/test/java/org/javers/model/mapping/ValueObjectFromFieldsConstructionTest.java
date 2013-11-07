package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author pawel szymczyk
 */
public class ValueObjectFromFieldsConstructionTest extends ValueObjectConstructionTest{

    @Before
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner(typeMapper);
        factory = new ValueObjectFactory(scanner);
    }
}
