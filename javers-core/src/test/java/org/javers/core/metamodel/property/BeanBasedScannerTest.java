package org.javers.core.metamodel.property;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class BeanBasedScannerTest extends PropertyScannerTest {

    @Before
    public void setUp() {
        propertyScanner = new BeanBasedPropertyScanner();
    }

    @Test
    public void shouldScanPrivateGetters() {
        //when
        List<Property> properties = propertyScanner.scan(ManagedClass.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("privateProperty");
    }
}
