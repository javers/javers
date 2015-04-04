package org.javers.repository.jql;

import org.javers.common.validation.Validate;

/**
 * Created by bartosz.walacik on 2015-04-04.
 */
class ClassFilter extends Filter {
    private final Class requiredClass;

    ClassFilter(Class aClass) {
        Validate.argumentIsNotNull(aClass);
        this.requiredClass = aClass;
    }

    public Class getRequiredClass() {
        return requiredClass;
    }

    @Override
    public String toString() {
        return "class=" + requiredClass.getName();
    }
}
