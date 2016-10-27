package org.javers.core.model

import org.javers.core.metamodel.annotation.IgnoreDeclaredProperties

/**
 * Created by Ian Agius
 */
@IgnoreDeclaredProperties
class DummyIgnoredPropertiesType extends DummyUser{

    int propertyThatShouldBeIgnored

    DummyIgnoredPropertiesType(String name, int propertyThatShouldBeIgnored) {
        super(name)
        this.propertyThatShouldBeIgnored = propertyThatShouldBeIgnored
    }

    DummyIgnoredPropertiesType(String name, int propertyWithTransientAnn, int propertyWithDiffIgnoreAnn, int propertyThatShouldBeIgnored) {
        this.name = name
        this.propertyWithTransientAnn = propertyWithTransientAnn
        this.propertyWithDiffIgnoreAnn = propertyWithDiffIgnoreAnn
        this.propertyThatShouldBeIgnored = propertyThatShouldBeIgnored
    }

    static DummyIgnoredPropertiesType dummyIgnoredPropertiesType(String name, int propertyThatShouldBeIgnored) {
        return new DummyIgnoredPropertiesType(name, propertyThatShouldBeIgnored);
    }

}
