package org.javers.core.metamodel.property

import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle
import org.javers.core.model.DummyUser

import static PropertyScanAssert.assertThat

/**
 * @author pawel szymczyk
 */
class FieldBasedScannerTest extends PropertyScannerTest {

    def setupSpec() {
        propertyScanner = JaversTestBuilder.javersTestAssembly(MappingStyle.FIELD).propertyScanner
        assert propertyScanner instanceof FieldBasedPropertyScanner
        propertyScanner
    }

    def "should ignore transient field"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasntGotProperty("someTransientField")
    }
}
