package org.javers.core.metamodel.clazz;

import org.javers.core.diff.custom.CustomValueComparator;

import java.util.Optional;

/**
 * @author bartosz walacik
 */
public class ValueDefinition extends ClientsClassDefinition {
    private final Optional<CustomValueComparator> customValueComparator;

    public ValueDefinition(Class<?> clazz) {
        super(clazz);
        this.customValueComparator = Optional.empty();
    }

    public ValueDefinition(Class<?> clazz, CustomValueComparator customValueComparator) {
        super(clazz);
        this.customValueComparator = Optional.of(customValueComparator);
    }

    public Optional<CustomValueComparator> getComparator() {
        return customValueComparator;
    }
}
