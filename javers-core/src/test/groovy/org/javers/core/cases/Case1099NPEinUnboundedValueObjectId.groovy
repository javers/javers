package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification


class Case1099NPEinUnboundedValueObjectId extends Specification {

    public class E {
        String profile
        List<String> roles

        E(String profile, List<String> roles) {
            this.profile = profile
            this.roles = roles
        }
    }

    class A {
          Map<String, List<E>> map
     }

    def "should support Map from String to List of ValueObjects" () {
        given:
        Javers javers = JaversBuilder.javers().build()

        def map = [
            "key1" : [ new E("p1", ["teacher", "director"]),
                       new E("p3", ["manager", "director"])
                     ],
            "key2" : [ new E("p2", ["student", "pupil"]) ]
        ]

        def a = new A(map: map)

        when:
        def commit = javers.commit("a", a)
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(A).build())

        then:
        snapshots
    }
}
