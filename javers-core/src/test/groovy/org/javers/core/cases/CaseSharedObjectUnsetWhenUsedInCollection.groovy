package org.javers.core.cases

import groovy.transform.EqualsAndHashCode
import org.javers.core.JaversBuilder
import spock.lang.Specification

class CaseSharedObjectUnsetWhenUsedInCollection extends Specification {

    @EqualsAndHashCode
    class DeepComplexProperty {
        String value
    }

    @EqualsAndHashCode
    class ComplexProperty {
        String value
        DeepComplexProperty deepProperty
    }

    @EqualsAndHashCode
    class ListItem {
        String name
        ComplexProperty complexProperty
    }

    @EqualsAndHashCode
    class TopLevelClass {
        ComplexProperty complexProperty
        List<ListItem> items
    }

    def "should compare shared nested property on fields"() {
        given:
        def javers = JaversBuilder.javers().build()

        def l1 = new TopLevelClass(
                complexProperty: new ComplexProperty(value: "value1", deepProperty: new DeepComplexProperty(value: "value2")),
                items: [
                        new ListItem(name: "name1", complexProperty: new ComplexProperty(value: "value1", deepProperty: new DeepComplexProperty(value: "value2")))
                ])

        def complexProperty = new ComplexProperty(value: "value1", deepProperty: new DeepComplexProperty(value: "value2"))
        def l2 = new TopLevelClass(
                complexProperty: complexProperty,
                items: [
                        new ListItem(name: "name1", complexProperty: complexProperty)
                ])

        when:
        def diff = javers.compare(l1, l2)

        then:
        l1.equals(l2)
        diff.changes.isEmpty()
    }
}
