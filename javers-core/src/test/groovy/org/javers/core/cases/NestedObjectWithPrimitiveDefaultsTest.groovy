package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.InitialValueChange
import org.javers.core.diff.changetype.PropertyChange
import org.javers.core.diff.changetype.TerminalValueChange
import spock.lang.Specification
import spock.lang.Unroll

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
class NestedObjectWithPrimitiveDefaultsTest extends Specification {

    class Item {
        int number

        Item(int number) {
            this.number = number
        }
    }

    class ItemParent {
        Item item

        ItemParent() {
        }

        ItemParent(item) {
            this.item = item
        }
    }

    def "should recognize removed nested object with a different value then Defaults.int.class with default ignoring config"() {
        given:
        def javers = JaversBuilder.javers().withUsePrimitiveDefaults(false).build()
        def item = new Item(1)
        def itemParentA = new ItemParent(item)
        def itemParentB = new ItemParent()

        when:
        def diff = javers.compare(itemParentA, itemParentB)

        println(diff.prettyPrint())

        then:
        diff.getChangesByType(PropertyChange).size() == 1
    }

    @Unroll
    def "should recognize #what nested object with same value as Java defaults when enabled"() {
        given:
        def javers = JaversBuilder.javers()
                //.withInitialChanges(false)
                //.withTerminalChanges(false)
                .withUsePrimitiveDefaults(false).build()

        when:
        def diff = javers.compare(itemParentA, itemParentB)

        println(diff.prettyPrint())

        then:
        diff.changes.size() == 1
        diff.changes[0].class == expectedChange

        where:
        what << ['removed','added']
        itemParentA <<    [ new ItemParent(new Item(0)), new ItemParent()]
        itemParentB <<    [ new ItemParent(),            new ItemParent(new Item(0))]
        expectedChange << [ TerminalValueChange,         InitialValueChange]
    }

    @Unroll
    def "should not recognize #what nested object with same value as Java defaults with default config"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def diff = javers.compare(itemParentA, itemParentB)

        println(diff.prettyPrint())

        then:
        diff.getChangesByType(PropertyChange).size() == 0

        where:
        what << ['removed','added']
        itemParentA <<    [ new ItemParent(new Item(0)), new ItemParent()]
        itemParentB <<    [ new ItemParent(),            new ItemParent(new Item(0))]
    }

}
