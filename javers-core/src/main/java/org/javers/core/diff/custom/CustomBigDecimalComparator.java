package org.javers.core.diff.custom;

import org.javers.core.JaversBuilder;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * Compares BigDecimals with custom precision.
 * Before comparing, values are rounded (HALF_UP) to required scale.
 * <br/><br/>
 *
 * Usage example:
 * <pre>
 * JaversBuilder.javers()
 *     .registerValue(BigDecimal.class, new CustomBigDecimalComparator(2))
 *     .build();
 * </pre>
 *
 * @see JaversBuilder#registerValue(Class, CustomValueComparator)
 * @author bartosz walacik
 */
public class CustomBigDecimalComparator implements CustomValueComparator<BigDecimal>{
    private int significantDecimalPlaces;

    public CustomBigDecimalComparator(int significantDecimalPlaces) {
        this.significantDecimalPlaces = significantDecimalPlaces;
    }

    @Override
    public boolean equals(BigDecimal a, BigDecimal b) {
        return round(a).equals(round(b));
    }

    @Override
    public String toString(BigDecimal value) {
        return round(value).toString();
    }

    private BigDecimal round(BigDecimal val) {
        return val.setScale(significantDecimalPlaces, ROUND_HALF_UP);
    }
}
