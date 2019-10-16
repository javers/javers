package org.javers.core.diff.custom;

import java.math.BigDecimal;

public class BigDecimalComparatorWithFixedEquals implements CustomValueComparator<BigDecimal> {
    @Override
    public boolean equals(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    @Override
    public String toString(BigDecimal value) {
        return value.stripTrailingZeros().toString();
    }
}
