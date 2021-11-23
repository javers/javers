package org.javers.core.diff.custom;

import com.google.common.base.Strings;
import org.javers.core.JaversBuilder;

import java.util.Objects;

/**
 * Compares Strings treating empty and null strings as equal.
 * <br/><br/>
 *
 * Usage example:
 * <pre>
 * JaversBuilder.javers()
 *     .registerValue(String.class, new CustomEmptyStringComparator())
 *     .build();
 * </pre>
 *
 * @see JaversBuilder#registerValue(Class, CustomValueComparator)
 */
public class CustomEmptyStringComparator extends NullHandlingCustomValueComparator<String> {

    @Override
    public boolean equals(String a, String b) {
        return Objects.equals(Strings.emptyToNull(a), Strings.emptyToNull(b));
    }

    @Override
    public String toString(String value) {
        return Strings.nullToEmpty(value);
    }
}
