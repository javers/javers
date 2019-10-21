package org.javers.core.diff.custom;

import java.math.BigDecimal;

/**
 * Compares BigDecimals in the right way &mdash; ignoring trailing zeros.
 * <br/><br/>
 *
 * Usage example:
 *
 * <pre>
 * JaversBuilder.javers()
 *     .registerValue(BigDecimal.class, new BigDecimalComparatorWithFixedEquals())
 *     .build();
 * </pre>
 */
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
