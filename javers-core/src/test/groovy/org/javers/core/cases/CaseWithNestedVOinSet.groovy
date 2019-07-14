package org.javers.core.cases

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification
import static java.util.UUID.randomUUID

/**
 * see https://github.com/javers/javers/issues/795
 */
class CaseWithNestedVOinSet extends Specification {

    def javers = JaversBuilder
            .javers()
            .registerValueObject(IngressRule)
            .registerValueObject(Port)
            .build()

    def "can detect new element added to set property"() {
        given:
        def a = new Firewall(
                1,
                "foo", [
                    new IngressRule("bar", new Port(80)),
                    new IngressRule("baz", new Port(80))
                ].toSet()
        )

        def b = new Firewall(
                a.id,
                a.name,
                a.ingressRules + new IngressRule("bar", new Port(443))
        )

        when:
        def diff = javers.compare(a, b)
        print diff

        then:
        diff.hasChanges()
        diff.changes.any { change ->
            change instanceof SetChange && change.changes.any { elementChange ->
                elementChange instanceof ValueAdded
            }
        }
    }
}

@TupleConstructor
class Firewall {
    @Id
    final int id
    final String name
    final Set<IngressRule> ingressRules
}

@ToString
@TupleConstructor
class IngressRule {
    final String name
    final Port port
}

@ToString
@TupleConstructor
class Port {
    final int number
}
