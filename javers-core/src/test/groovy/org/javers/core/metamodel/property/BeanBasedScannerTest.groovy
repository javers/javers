package org.javers.core.metamodel.property
/**
 * @author pawel szymczyk
 */
class BeanBasedScannerTest extends PropertyScannerTest {

    def setupSpec() {
        propertyScanner = new BeanBasedPropertyScanner()
    }
}
