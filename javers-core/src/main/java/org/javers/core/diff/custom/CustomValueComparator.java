package org.javers.core.diff.custom;

import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.type.ValueType;

/**
 * Can be used for comparing {@link ValueType}.
 * Register a CustomValueComparator for your class
 * using {@link JaversBuilder#registerValue(Class, CustomValueComparator)}
 */
@FunctionalInterface
public interface CustomValueComparator<T> {
    boolean equals(T left, T right);
}
