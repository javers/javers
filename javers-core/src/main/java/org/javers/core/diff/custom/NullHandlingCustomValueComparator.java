package org.javers.core.diff.custom;

import org.javers.core.metamodel.type.ValueType;

/**
 * A custom comparator for {@link ValueType} classes
 * to be used instead of default {@link Object#equals(Object)}.
 * In contrast to {@link CustomValueComparator} this will
 * cause Javers' null safety checking to be bypassed, allowing
 * for the comparator to consider nulls as equal to other values.
 * This also places the burden of handling nulls safely on the
 * implementer.
 * <br/><br/>
 *
 * Example implementation: {@link CustomEmptyStringComparator}
 * <br/><br/>
 *
 * <b>Usage</b>:
 * <pre>
 * JaversBuilder.javers()
 *              .registerValue(String.class, new CustomEmptyStringComparator())
 *              .build()
 * </pre>
 * @param <T> Value Type
 * @see <a href="http://javers.org/documentation/domain-configuration/#ValueType">http://javers.org/documentation/domain-configuration/#ValueType</a>
 * @see <a href="https://javers.org/documentation/diff-configuration/#custom-comparators">https://javers.org/documentation/diff-configuration/#custom-comparators</a>
 */
public abstract class NullHandlingCustomValueComparator<T> implements CustomValueComparator<T> {

    @Override
    public final boolean handlesNulls() {
        return true;
    }
}
