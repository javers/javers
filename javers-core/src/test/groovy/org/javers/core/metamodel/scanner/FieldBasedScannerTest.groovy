package org.javers.core.metamodel.scanner

import org.javers.core.metamodel.annotation.DiffIgnoreProperties
import org.javers.core.metamodel.clazz.JaversEntity
import org.javers.core.model.DummyUser

import static PropertyScanAssert.assertThat

/**
 * @author pawel szymczyk
 */
class FieldBasedScannerTest extends PropertyScannerTest {

    @DiffIgnoreProperties(["field1", "field2"])
    class EntityWithFieldIgnoredInList extends JaversEntity {
        String field1;
        String field2;
        String field3;
    }

    def setupSpec() {
        propertyScanner = new FieldBasedPropertyScanner(new AnnotationNamesProvider())
    }

    def "should ignore transient field"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasntGotProperty("someTransientField")
    }

    def "should ignore multiple fields by @DiffIgnoreFields"() {
        when:
        def properties = propertyScanner.scan(EntityWithFieldIgnoredInList)

        then:
        assertThat(properties).hasProperty("field1").isTransient()
        assertThat(properties).hasProperty("field2").isTransient()
        assertThat(properties).hasProperty("field3").isNotTransient()
    }
}
