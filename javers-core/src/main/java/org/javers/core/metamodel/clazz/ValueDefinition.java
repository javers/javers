package org.javers.core.metamodel.clazz;

import org.javers.core.diff.custom.CustomValueComparator;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author bartosz walacik
 */
public class ValueDefinition extends ClientsClassDefinition {
    private CustomValueComparator customValueComparator;
    private Function<Object, String> toStringFunction;

    public ValueDefinition(Class<?> clazz) {
        super(clazz);
    }

    public void setCustomValueComparator(CustomValueComparator customValueComparator) {
        this.customValueComparator = customValueComparator;
    }

    public void setToStringFunction(Function<Object, String> toStringFunction) {
        this.toStringFunction = toStringFunction;
    }

    public CustomValueComparator getComparator() {
        return customValueComparator;
    }

    public Function<Object, String> getToStringFunction() {
        return toStringFunction;
    }
}
