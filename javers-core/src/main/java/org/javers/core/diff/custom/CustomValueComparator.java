package org.javers.core.diff.custom;

import org.javers.core.metamodel.type.CustomType;
import org.javers.core.metamodel.type.ValueType;

/**
 * Registers a custom comparator for your {@link ValueType} class
 * to be used instead of default {@link Object#equals(Object)}.
 *
 * <br/><br/>
 *
 * <b>Usage</b>:
 * <pre>
 * JaversBuilder.javers()
 *     .registerValue(BigDecimal.class, new CustomBigDecimalComparator(2)).build()
 * </pre>
 */
public interface CustomValueComparator<T> {
    /**
     * Called by JaVers to compare two Values.
     */
    boolean equals(T a, T b);

    String toString(T value);
}
