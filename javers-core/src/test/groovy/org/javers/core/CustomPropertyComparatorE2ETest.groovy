package org.javers.core

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import org.javers.core.diff.changetype.PropertyChange
import org.javers.core.diff.custom.CustomBigDecimalComparator
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.property.Property
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUserWithValues
import org.javers.core.model.GuavaObject
import spock.lang.Specification

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

    def "should support custom comparator for Value types"(){
        given:
        def left = new DummyUserWithValues("user", 10.11)
        def right = new DummyUserWithValues("user", 10.12)

        when:
        def javers = JaversBuilder.javers().build()

        then:
        javers.compare(left,right).hasChanges()

        when:
        javers = JaversBuilder.javers()
                .registerCustomComparator(new CustomBigDecimalComparator(1), BigDecimal).build()

        then:
        !javers.compare(left,right).hasChanges()
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
            affectedCdoId instanceof UnboundedValueObjectId
            propertyName == "multimap"
            changes[0].key == "a"
            changes[0].leftValue == 1
            changes[0].rightValue == 2
        }
    }
}
