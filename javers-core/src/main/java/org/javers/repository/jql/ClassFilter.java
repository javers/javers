package org.javers.repository.jql;

import org.javers.common.collections.Sets;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;

import java.util.Set;

/**
 * @author bartosz.walacik
 */
class ClassFilter extends Filter {
    private final Set<Class> requiredClasses;

    ClassFilter(Set<Class> requiredClasses) {
        Validate.argumentIsNotNull(requiredClasses);
        this.requiredClasses = requiredClasses;
    }

    public Set<Class> getRequiredClasses() {
        return requiredClasses;
    }

    @Override
    public String toString() {
        return "classes=" + ToStringBuilder.setToString(
                Sets.transform(requiredClasses, javaClass -> javaClass.getName())
        );
    }
}
