package org.javers.core.metamodel.scanner
/**
 * @author pawel szymczyk
 */
class BeanBasedScannerTest extends PropertyScannerTest {

    def setupSpec() {
        propertyScanner = new BeanBasedPropertyScanner(new AnnotationNamesProvider())
    }
}
