package org.javers.core.metamodel.property;

import org.fest.assertions.api.AbstractAssert;
import org.javers.test.assertion.Assertions;

import java.util.List;

import static org.fest.assertions.api.Fail.fail;

/**
 * @author pawel szymczyk
 */
public class PropertiesAssert extends AbstractAssert<PropertiesAssert, List<Property>> {

    protected PropertiesAssert(List<Property> actual) {
        super(actual, PropertiesAssert.class);
    }

    public static PropertiesAssert assertThat(List<Property> actual) {
        return new PropertiesAssert(actual);
    }

    public PropertyAssert hasProperty(String withName) {
        Property found = getProperty(withName);
        Assertions.assertThat(found).isNotNull();

        return PropertyAssert.assertThat(found);
    }

    public Property getProperty(String withName) {
        for (Property property : actual) {
            if (property.getName().equals(withName)) {
                return property;
            }
        }
        return null;
    }

    public PropertyAssert hasntGotProperty(String withName) {
        Property found = getProperty(withName);
        Assertions.assertThat(found).isNull();

        return PropertyAssert.assertThat(found);
    }

    public PropertiesAssert hasId() {
        for (Property property : actual) {
            if (property.looksLikeId()) return this;
        }
        fail("Id not found");
        return null;
    }
}
