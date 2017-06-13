package org.javers.core.diff.custom;

import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.type.ValueType;

/**
 * Can be used only for comparing {@link ValueType}.
 *
 * @see JaversBuilder#registerValue(Class, CustomValueComparator)
 */
@FunctionalInterface
public interface CustomValueComparator<T> {
    boolean equals(T left, T right);
}
