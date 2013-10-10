package org.javers.model.mapping.util.managedClassPropertyScanner;

import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author pawel szymczyk
 */
public class FieldBasedScannerTest extends ScannerTest{

    @BeforeMethod
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        scanner = FieldBasedScanner.getInstane(typeMapper);
    }
}
