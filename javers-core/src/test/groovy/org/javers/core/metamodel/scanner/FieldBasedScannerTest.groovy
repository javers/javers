package org.javers.core.metamodel.scanner

import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails

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

    def "should scan PropertyName property for field based scanner"() {
        when:
        def properties = propertyScanner.scan(DummyUserDetails)

        then:
        assertThat(properties).hasProperty("Customized Property")
                .hasJavaType(String)
    }

}
