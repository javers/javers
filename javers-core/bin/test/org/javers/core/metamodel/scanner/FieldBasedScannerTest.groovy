package org.javers.core.metamodel.scanner

import org.javers.core.model.DummyUser

import static PropertyScanAssert.assertThat

/**
 * @author pawel szymczyk
 */
class FieldBasedScannerTest extends PropertyScannerTest {

    def setupSpec() {
        propertyScanner = new FieldBasedPropertyScanner(new AnnotationNamesProvider())
    }

    def "should ignore transient field"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasntGotProperty("someTransientField")
    }
}
