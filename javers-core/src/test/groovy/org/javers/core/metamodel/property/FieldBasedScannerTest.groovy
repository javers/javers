package org.javers.core.metamodel.property
/**
 * @author pawel szymczyk
 */
class FieldBasedScannerTest extends PropertyScannerTest {

    def setupSpec() {
        propertyScanner = new FieldBasedPropertyScanner()
    }
}
