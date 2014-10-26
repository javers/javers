package org.javers.core.metamodel.property

import org.javers.core.model.DummyUser

import static org.javers.core.metamodel.property.PropertiesAssert.assertThat

/**
 * @author pawel szymczyk
 */
class FieldBasedScannerTest extends PropertyScannerTest {

    def setupSpec() {
        propertyScanner = new FieldBasedPropertyScanner()
    }

    def "should ignore transient field"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasntGotProperty("someTransientField")
    }
}
