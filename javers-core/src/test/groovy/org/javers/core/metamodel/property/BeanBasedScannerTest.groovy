package org.javers.core.metamodel.property

import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle

/**
 * @author pawel szymczyk
 */
class BeanBasedScannerTest extends PropertyScannerTest {

    def setupSpec() {
        propertyScanner = JaversTestBuilder.javersTestAssembly(MappingStyle.BEAN).propertyScanner
        assert propertyScanner instanceof  BeanBasedPropertyScanner
        propertyScanner
    }
}
