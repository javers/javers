package org.javers.core

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import org.javers.core.diff.changetype.PropertyChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.custom.CustomBigDecimalComparator
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.property.Property
import org.javers.core.metamodel.type.ValueType
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUserWithValues
import org.javers.core.model.GuavaObject
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.model.DummyAddress.Kind.HOME
import static org.javers.core.model.DummyAddress.Kind.OFFICE

/**
 * @author bartosz.walacik
 */
class CustomPropertyComparatorE2ETest extends Specification {

    def "should support more than one custom comparator"(){
        given:
        def left =  new DummyAddress(city:"NY", kind:HOME)
        def right = new DummyAddress(city:"Paris", kind:OFFICE)

        when:
        def javers = JaversBuilder.javers().build()

        then:
        javers.compare(left,right).changes.size() == 2

        when:
        javers = JaversBuilder.javers()
                .registerCustomComparator( new DummyCustomPropertyComparator(), String)
                .registerCustomComparator( new DummyCustomPropertyComparator(), DummyAddress.Kind)
                .build()

        then:
        javers.compare(left,right).changes.size() == 0
    }

    private class DummyCustomPropertyComparator implements CustomPropertyComparator {
        PropertyChange compare(Object left, Object right, GlobalId affectedId, Property property) {
            null
        }
    }

    @Unroll
    def "should support custom comparator for Value types like BigDecimal"(){
        given:
        def left = new DummyUserWithValues("user", 10.11)
        def right = new DummyUserWithValues("user", 10.12)

        when:
        def javers = JaversBuilder.javers()
                .registerCustomComparator(new CustomBigDecimalComparator(precision), BigDecimal).build()
        def changes = javers.compare(left,right).changes

        then:
        changes.size() == expectedChanges
        javers.getTypeMapping(BigDecimal) instanceof ValueType

        if (expectedChanges) {
            assert changes[0] instanceof ValueChange
            assert changes[0].left == 10.11
            assert changes[0].right == 10.12
        }


        where:
        precision | expectedChanges
        0         | 0
        1         | 0
        2         | 1
    }

    def "should support custom comparator for custom types"() {
        given:
        def left =  new GuavaObject(multimap: Multimaps.forMap(["a":1]))
        def right = new GuavaObject(multimap: Multimaps.forMap(["a":2]))
        def javers = JaversBuilder.javers()
                .registerCustomComparator(new CustomMultimapFakeComparator(), Multimap).build()

        when:
        def diff = javers.compare(left,right)

        then:
        diff.changes.size() == 1
        with(diff.changes[0]) {
            affectedGlobalId instanceof UnboundedValueObjectId
            propertyName == "multimap"
            changes[0].key == "a"
            changes[0].leftValue == 1
            changes[0].rightValue == 2
        }
    }

    @Unroll
    def "should support custom comparator for objects stored in #containerType"() {
        when:
        def javers = JaversBuilder.javers().build()

        then:
        javers.compare(left, right).changes.size() == 1

        when:
        javers = JaversBuilder.javers()
                .registerValueChangeCustomComparator(new DummyCustomPropertyComparator(), String)
                .build()

        then:
        javers.compare(left, right).changes.size() == 0

        where:
        left           | right          | containerType
        ["abc"]        | ["def"]        | List.class.simpleName
        [1, "abc"]     | [1, "def"]     | Map.class.simpleName
    }
}
