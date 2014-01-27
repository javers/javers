package org.javers.core.metamodel.property
/**
 * @author pawel szymczyk
 */
class BeanBasedScannerTest extends PropertyScannerTest {

    def setup() {
        propertyScanner = new BeanBasedPropertyScanner()
    }
}
