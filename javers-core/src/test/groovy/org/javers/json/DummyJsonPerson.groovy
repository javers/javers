package org.javers.json

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * @author bartosz walacik
 */
class DummyJsonPerson {
    String firstName
    String lastName

    public DummyJsonPerson(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    boolean equals(Object o) {
        EqualsBuilder.reflectionEquals(this, o)
    }

    @Override
    int hashCode() {
        HashCodeBuilder.reflectionHashCode(this)
    }
}
