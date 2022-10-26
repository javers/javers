package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.custom.BigDecimalComparatorWithFixedEquals
import org.javers.core.json.BasicStringTypeAdapter
import org.javers.core.metamodel.type.ValueType
import spock.lang.Specification

import java.math.RoundingMode

import static org.javers.core.JaversBuilder.javers

/**
 * @author catautobox
 */
class JaversBuilderIgnoresCustomValueComparatorWhenRegisteringValueTypeAdapter extends Specification {

    def "should use registered custom comparator for value type after registering a value type adapter"() {
        given:
        JaversBuilder javersBuilder = javers()
                .registerValue(BigDecimal, new BigDecimalComparatorWithFixedEquals())
                .registerValueTypeAdapter(new Scale2HalfUpRoundingBigDecimalTypeAdapter())

        when:
        def javers = javersBuilder.build()

        then:
        javers.jsonConverter.toJson(new BigDecimal("0.005")) == '"0.01"'
        ((ValueType) javers.getTypeMapping(BigDecimal)).hasCustomValueComparator()
    }

    class Scale2HalfUpRoundingBigDecimalTypeAdapter extends BasicStringTypeAdapter {

        @Override
        String serialize(Object sourceValue) {
            return ((BigDecimal) sourceValue).setScale(2, RoundingMode.HALF_UP).toString()
        }

        @Override
        Object deserialize(String serializedValue) {
            return new BigDecimal(serializedValue).setScale(2, RoundingMode.HALF_UP)
        }

        @Override
        Class getValueType() {
            return BigDecimal
        }
    }
}
