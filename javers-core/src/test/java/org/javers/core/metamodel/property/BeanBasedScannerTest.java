package org.javers.core.metamodel.property;

import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author pawel szymczyk
 */
public class BeanBasedScannerTest extends PropertyScannerTest {

    @Before
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        propertyScanner = new BeanBasedPropertyScanner(typeMapper);
    }

    @Test
    public void shouldScanPrivateGetters() {
        //when
        List<Property> properties = propertyScanner.scan(ManagedClass.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("privateProperty");
    }
}
