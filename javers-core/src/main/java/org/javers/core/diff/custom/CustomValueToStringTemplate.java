package org.javers.core.diff.custom;


import java.util.Objects;

@FunctionalInterface
public interface CustomValueToStringTemplate<T> extends CustomValueComparator<T> {
    @Override
    default  boolean equals(T a, T b) {
        return Objects.equals(a, b);
    }
}
