package org.javers.model.mapping;

import org.fest.assertions.api.AbstractAssert;
import org.javers.test.assertion.Assertions;

import java.util.List;

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
}
