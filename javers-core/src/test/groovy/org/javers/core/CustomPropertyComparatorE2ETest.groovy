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
}
