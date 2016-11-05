package org.javers.core.model

import org.javers.core.metamodel.annotation.IgnoreDeclaredProperties

/**
 * Created by Ian Agius
 */
@IgnoreDeclaredProperties
class DummyIgnoredPropertiesType extends DummyUser{
    private int propertyThatShouldBeIgnored
    private int anotherIgnored

    int getPropertyThatShouldBeIgnored() {
        return propertyThatShouldBeIgnored
    }

    int getAnotherIgnored() {
        return anotherIgnored
    }
}
