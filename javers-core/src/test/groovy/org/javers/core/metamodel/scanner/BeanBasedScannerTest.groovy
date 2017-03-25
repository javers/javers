package org.javers.core.metamodel.scanner

import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUserDetails

import static org.javers.core.metamodel.scanner.PropertyScanAssert.assertThat

/**
 * @author pawel szymczyk
 */
class BeanBasedScannerTest extends PropertyScannerTest {

    def setupSpec() {
        propertyScanner = new BeanBasedPropertyScanner(new AnnotationNamesProvider())
    }

    def "should scan PropertyName property for bean based scanner"() {
        when:
        def properties = propertyScanner.scan(DummyUserDetails)

        then:
        assertThat(properties).hasProperty("customizedProperty")
                .hasJavaType(String)
    }

}
