package org.javers.core.diff.custom;

import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.metamodel.type.ValueType;

/**
 * Registers a custom comparator for {@link ValueType} class
 * to be used instead of default {@link Object#equals(Object)}.
 * <br/><br/>
 *
 * Example implementation: {@link CustomBigDecimalComparator}
 * <br/><br/>
 *
 * <b>Usage</b>:
 * <pre>
 * JaversBuilder.javers()
 *              .registerValue(BigDecimal.class, new CustomBigDecimalComparator(2))
 *              .build()
 * </pre>
 */
public interface CustomValueComparator<T> {
    /**
     * Called by JaVers to compare two Values.
     *
     * @param a not null
     * @param b not null
     */
    boolean equals(T a, T b);

    /**
     * Used instead of {@link Object#hashCode()} when Values are compared in hashing contexts:
     *
     * <ul>
     *     <li>Sets with Values</li>
     *     <li>Lists with Values compared as {@link ListCompareAlgorithm#AS_SET}</li>
     *     <li>Maps with Values as keys</li>
     * </ul>
     *
     * Implementation should be aligned with {@link #equals(Object, Object)}
     * in the same way like {@link Object#hashCode()} should be aligned with {@link Object#equals(Object)}.
     *
     * @param value not null
     */
    String toString(T value);
}
