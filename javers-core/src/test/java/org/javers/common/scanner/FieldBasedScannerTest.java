package org.javers.common.scanner;

import org.javers.model.mapping.FieldBasedPropertyScanner;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author pawel szymczyk
 */
public class FieldBasedScannerTest extends PropertyScannerTest {

    @BeforeMethod
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        propertyScanner = FieldBasedPropertyScanner.getInstane(typeMapper);
    }
}
