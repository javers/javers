package org.javers.core.metamodel.scanner

import org.javers.core.metamodel.property.Property

/**
 * @author pawel szymczyk
 */
class PropertyScanAssert {
    private PropertyScan actual

    private PropertyScanAssert(PropertyScan actual) {
        this.actual = actual
    }

    static PropertyScanAssert assertThat(PropertyScan actual) {
        new PropertyScanAssert(actual)
    }

    PropertyAssert hasProperty(String withName) {
        Property found = getProperty(withName)
        assert found != null

        PropertyAssert.assertThat(found)
    }

    void hasntGotProperty(String withName) {
        assert getProperty(withName) == null
    }

    Property getProperty(String withName) {
        def found = actual.properties.grep{it.name == withName}
        assert found.size() <= 1
        found.size() == 1 ? found[0] : null
    }
}
