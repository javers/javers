package org.javers.model.mapping;

import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyNetworkAddress;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.test.builder.TypeMapperTestBuilder;
import org.testng.annotations.BeforeMethod;
import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author pawel szymczyk
 */
public class ValueObjectFromBeanConstructionTest extends ValueObjectConstructionTest{

    @BeforeMethod
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        factory = new BeanBasedValueObjectFactory(typeMapper);
    }
}
