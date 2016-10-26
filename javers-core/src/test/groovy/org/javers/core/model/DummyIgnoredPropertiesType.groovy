package org.javers.core.model

import org.javers.core.metamodel.annotation.IgnoreDeclaredProperties

/**
 * Created by Ian Agius
 */
@IgnoreDeclaredProperties
class DummyIgnoredPropertiesType extends DummyUser{

    int propertyThatShouldBeIgnored

    static DummyIgnoredPropertiesType dummyIgnoredPropertiesType(String name, int propertyThatShouldBeIgnored) {
        return new DummyIgnoredPropertiesType(name:name, propertyThatShouldBeIgnored:propertyThatShouldBeIgnored);
    }

}
