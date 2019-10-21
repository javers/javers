package org.javers.core.metamodel.clazz;

import org.javers.core.diff.custom.CustomValueComparator;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author bartosz walacik
 */
public class ValueDefinition extends ClientsClassDefinition {
    private CustomValueComparator customValueComparator;

    public ValueDefinition(Class<?> clazz) {
        super(clazz);
    }

    public void setCustomValueComparator(CustomValueComparator customValueComparator) {
        this.customValueComparator = customValueComparator;
    }

    public CustomValueComparator getComparator() {
        return customValueComparator;
    }
}
