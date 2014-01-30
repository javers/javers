package org.javers.core.metamodel.property

/**
 * @author pawel szymczyk
 */
class PropertiesAssert {
    private List<Property> actual

    private PropertiesAssert(List<Property> actual) {
        this.actual = actual
    }

    static PropertiesAssert assertThat(List<Property> actual) {
        new PropertiesAssert(actual)
    }

    PropertyAssert hasProperty(String withName) {
        Property found = getProperty(withName)
        assert found != null

        PropertyAssert.assertThat(found)
    }

    void hasntGotProperty(String withName) {
        Property found = getProperty(withName)
        assert found == null
    }

    Property getProperty(String withName) {
        def found = actual.grep{it.name == withName}
        found.size() == 1 ? found[0] : null
    }
}
