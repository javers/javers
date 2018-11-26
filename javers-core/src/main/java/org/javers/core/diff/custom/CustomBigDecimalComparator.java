package org.javers.core.diff.custom;

import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import java.math.BigDecimal;
import java.util.Optional;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * Compares BigDecimals with custom precision.
 * Before comparing, values are rounded (HALF_UP) to required scale.
 * <br/><br/>
 *
 * Usage example:
 * <pre>
 * JaversBuilder.javers()
 *     .registerCustomComparator(new CustomBigDecimalComparator(2), BigDecimal).build();
 * </pre>
 *
 * @author bartosz walacik
 */
public class CustomBigDecimalComparator implements CustomPropertyComparator<BigDecimal, ValueChange>{
    private int significantDecimalPlaces;

    public CustomBigDecimalComparator(int significantDecimalPlaces) {
        this.significantDecimalPlaces = significantDecimalPlaces;
    }

    @Override
    public Optional<ValueChange> compare(BigDecimal left, BigDecimal right, GlobalId affectedId, Property property)
    {
        BigDecimal leftRounded = left.setScale(significantDecimalPlaces, ROUND_HALF_UP);
        BigDecimal rightRounded = right.setScale(significantDecimalPlaces, ROUND_HALF_UP);

        if (leftRounded.equals(rightRounded)){
            return Optional.empty();
        }

        return Optional.of(new ValueChange(affectedId, property.getName(), left, right));
    }
}
