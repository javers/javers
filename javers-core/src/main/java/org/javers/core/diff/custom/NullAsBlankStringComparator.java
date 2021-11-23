package org.javers.core.diff.custom;

import org.javers.common.string.Strings;
import org.javers.core.JaversBuilder;

import java.util.Objects;

/**
 * Compares Strings treating blank and null strings as equal.
 * <br/><br/>
 *
 * Usage example:
 * <pre>
 * JaversBuilder.javers()
 *     .registerValue(String.class, new NullAsBlankStringComparator())
 *     .build();
 * </pre>
 *
 * @see JaversBuilder#registerValue(Class, CustomValueComparator)
 */
public class NullAsBlankStringComparator implements CustomValueComparator<String> {

    @Override
    public boolean equals(String a, String b) {
        return Objects.equals(Strings.emptyIfNull(a).trim(), Strings.emptyIfNull(b).trim());
    }

    @Override
    public String toString(String value) {
        return Strings.emptyIfNull(value).trim();
    }

    @Override
    public boolean handlesNulls() {
        return true;
    }
}
