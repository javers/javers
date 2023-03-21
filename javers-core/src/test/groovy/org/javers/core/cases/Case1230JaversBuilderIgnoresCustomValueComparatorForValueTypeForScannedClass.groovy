package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.custom.BigDecimalComparatorWithFixedEquals
import org.javers.core.metamodel.type.ManagedType
import org.javers.core.metamodel.type.ValueType
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers

/**
 * @author catautobox
 */
class Case1230JaversBuilderIgnoresCustomValueComparatorForValueTypeForScannedClass extends Specification {

    def "should use registered custom value comparator for value type property of scanned class"() {
        given:
        JaversBuilder javersBuilder = javers()
                .registerValue(BigDecimal, new BigDecimalComparatorWithFixedEquals())
                .scanTypeName(BigDecimalValueHolder)

        when:
        def javers = javersBuilder.build()

        then:
        ((ValueType) javers.getTypeMapping(BigDecimal)).hasCustomValueComparator()
        ((ValueType) ((ManagedType) javers.getTypeMapping(BigDecimalValueHolder)).getProperty("value").getType()).hasCustomValueComparator()
    }


    class BigDecimalValueHolder {
        BigDecimal value
    }
}
