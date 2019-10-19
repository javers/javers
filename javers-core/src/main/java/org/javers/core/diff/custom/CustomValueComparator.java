package org.javers.core.diff.custom;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.type.ValueType;

/**
 * A custom comparator for {@link ValueType} classes
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
 * @param <T> Value Type
 * @see <a href="http://javers.org/documentation/domain-configuration/#ValueType">http://javers.org/documentation/domain-configuration/#ValueType</a>
 * @see <a href="https://javers.org/documentation/diff-configuration/#custom-comparators">https://javers.org/documentation/diff-configuration/#custom-comparators</a>
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
     * This method has two roles.
     * First, it is used when Values are compared in hashing contexts.
     * Second, it is used to build Entity Ids from Values.
     *
     * <br/><br/>
     * <h2>Hashcode role</h2>
     *
     * When a Value class has custom <code>toString()</code>, it is used
     * instead of {@link Object#hashCode()} when comparing Values in hashing contexts, so:
     *
     * <ul>
     *     <li>Sets with Values</li>
     *     <li>Lists with Values compared as {@link ListCompareAlgorithm#AS_SET}</li>
     *     <li>Maps with Values as keys</li>
     * </ul>
     *
     * Custom <code>toString()</code> implementation should be aligned with custom {@link #equals(Object, Object)}
     * in the same way like {@link Object#hashCode()} should be aligned with {@link Object#equals(Object)}.
     *
     * <br/><br/>
     * <h2>Entity Id role</h2>
     *
     * Each Value can serve as an Entity Id.<br/>
     *
     * When a Value has custom <code>toString()</code>
     * function, it is used for creating {@link InstanceId} for Entities.
     * If a Value doesn't have a custom <code>toString()</code>
     * , default {@link ReflectionUtil#reflectiveToString(Object)}) is used.
     * <br/><br/>
     *
     * See full example <a href="https://github.com/javers/javers/blob/master/javers-core/src/test/groovy/org/javers/core/examples/CustomToStringExample.groovy">CustomToStringExample.groovy</a>.
     *
     * @param value not null
     */
    String toString(T value);
}
