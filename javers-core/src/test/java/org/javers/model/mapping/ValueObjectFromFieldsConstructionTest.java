package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author pawel szymczyk
 */
public class ValueObjectFromFieldsConstructionTest extends ValueObjectConstructionTest{

    @BeforeMethod
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        factory = new FieldBasedValueObjectFactory(typeMapper);
    }
}
