package org.javers.core.cases

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification

@ToString(includePackage = false)
class Order {                    // Entity
    @Id String id
    Set<OrderLine> lines                // unordered collection of ValueObjects
}

@ToString(includePackage = false)
class OrderLine {                // ValueObject (no @Id) -> needs a hash
    String product
    int qty
    ShippingInfo shipping               // nested ValueObject (a direct child)
    Set<Discount> discounts             // Set of Values
}

@ToString(includePackage = false)
class ShippingInfo {             // ValueObject
    String carrier
}

@EqualsAndHashCode(includes = ["code"])
@ToString(includePackage = false)
class Discount {                 // registered with registerValue(Discount)
    String code                  // NOT Comparable -> this is what breaks in #1509
}

/**
 * see https://github.com/javers/javers/issues/1509
 */
class Case1509NonComparableValuesInSetInVO extends Specification {

    def "should compare Orders when OrderLine holds a Set of non-Comparable ValueObjects"() {
        given:
        def javers = JaversBuilder.javers().build()

        def before = new Order(id: "o-1", lines: [line("pen", 2, "DHL", ["Discount A", "Discount B"])] as Set)
        def after  = new Order(id: "o-1", lines: [line("pen", 2, "DHL", ["Discount A"])] as Set)

        when:
        def diff = javers.compare(before, after)

        then:
        diff.hasChanges()
    }

    def "should compare Orders when OrderLine holds a Set of non-Comparable Values"() {
        given:
        def javers = JaversBuilder.javers()
                .registerValue(Discount)
                .build()

        def before = new Order(id: "o-1", lines: [line("pen", 2, "DHL", ["Discount A", "Discount B"])] as Set)
        def after  = new Order(id: "o-1", lines: [line("pen", 2, "DHL", ["Discount A"])] as Set)

        when:
        def diff = javers.compare(before, after)

        then:
        diff.hasChanges()
    }

    def "should not detect diff on reordering Values in Set in ValueObject"() {
        given:
        def javers = JaversBuilder.javers().registerValue(Discount).build()

        def before = new Order(id: "o-1", lines: [line("pen", 2, "DHL", ["Discount A", "Discount B"])] as Set)
        def after  = new Order(id: "o-1", lines: [line("pen", 2, "DHL", ["Discount B", "Discount A"])] as Set)

        when:
        def diff = javers.compare(before, after)

        then:
        !diff.hasChanges()
    }

    def "should not detect diff on reordering ValueObjects in Set in ValueObject"() {
        given:
        def javers = JaversBuilder.javers().build()

        def before = new Order(id: "o-1", lines: [line("pen", 2, "DHL", ["Discount C", "Discount B", "Discount A"])] as Set)
        def after  = new Order(id: "o-1", lines: [line("pen", 2, "DHL", ["Discount A", "Discount B", "Discount C"])] as Set)

        when:
        def diff = javers.compare(before, after)

        then:
        !diff.hasChanges()
    }

   OrderLine line(String product, int qty, String carrier, List<String> codes) {
        new OrderLine(product: product, qty: qty,
                shipping: new ShippingInfo(carrier: carrier),
                discounts: codes.collect { new Discount(code: it) } as Set)
    }


}
