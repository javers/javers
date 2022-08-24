package org.javers.core.nestedobjects

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.PropertyChange
import spock.lang.Specification

class NestedObjectTest extends Specification {
    /*
    When having two objects of which one has a child object, JaVers is not able to recognize the difference,
    when the properties of the child object have the same values as the corresponding default value in
    org.javers.common.collections.Defaults

    The first test ends successful, because the child Item has the value property `1`, which differs from the default
    in Defaults (0).

    The second test fails, because the child Item has the value property `0`, which is the same as the default in
    Defaults (0).

    In both tests the itemParentA, which has a child Item is compared to the itemParentB, which has no child Item.

    Please ensure, that removed nested objects are removed properly. I think the Problem is the FakeNode which uses the
    defaults from Defaults in the methods getDehydratedPropertyValue and getPropertyValue.

    The behaviour can be reproduced with other data types.
    */

    def "should recognize removed nested object with a different value then Defaults.int.class with default ignoring config"() {
        given:
        def javers = JaversBuilder.javers().whithUsePrimitiveDefaults(false).build()
        def item = new Item(1)
        def itemParentA = new ItemParent(item)
        def itemParentB = new ItemParent()

        when:
        def diff = javers.compare(itemParentA, itemParentB)

        println(diff.prettyPrint())

        then:
        diff.getChangesByType(PropertyChange).size() == 1
    }

    def "should recognize removed nested object with same value as Defaults.int.class with default ignoring config"() {
        given:
        def javers = JaversBuilder.javers().whithUsePrimitiveDefaults(false).build()
        def item = new Item(0)
        def itemParentA = new ItemParent(item)
        def itemParentB = new ItemParent()

        when:
        def diff = javers.compare(itemParentA, itemParentB)

        println(diff.prettyPrint())

        then:
        diff.getChangesByType(PropertyChange).size() == 1
    }

    def "should recognize added nested object with same value as Defaults.int.class with default ignoring config"() {
        given:
        def javers = JaversBuilder.javers().whithUsePrimitiveDefaults(false).build()
        def item = new Item(0)
        def itemParentA = new ItemParent()
        def itemParentB = new ItemParent(item)

        when:
        def diff = javers.compare(itemParentA, itemParentB)

        println(diff.prettyPrint())

        then:
        diff.getChangesByType(PropertyChange).size() == 1
    }

    def "should recognize removed nested object with a different value then Defaults.int.class with default config"() {
        given:
        def javers = JaversBuilder.javers().build()
        def item = new Item(1)
        def itemParentA = new ItemParent(item)
        def itemParentB = new ItemParent()

        when:
        def diff = javers.compare(itemParentA, itemParentB)

        println(diff.prettyPrint())

        then:
        diff.getChangesByType(PropertyChange).size() == 1
    }

    def "should recognize removed nested object with same value as Defaults.int.class with default config"() {
        given:
        def javers = JaversBuilder.javers().build()
        def item = new Item(0)
        def itemParentA = new ItemParent(item)
        def itemParentB = new ItemParent()

        when:
        def diff = javers.compare(itemParentA, itemParentB)

        println(diff.prettyPrint())

        then:
        diff.getChangesByType(PropertyChange).size() == 0
    }

    def "should recognize added nested object with same value as Defaults.int.class with default config"() {
        given:
        def javers = JaversBuilder.javers().build()
        def item = new Item(0)
        def itemParentA = new ItemParent()
        def itemParentB = new ItemParent(item)

        when:
        def diff = javers.compare(itemParentA, itemParentB)

        println(diff.prettyPrint())

        then:
        diff.getChangesByType(PropertyChange).size() == 0
    }

}
