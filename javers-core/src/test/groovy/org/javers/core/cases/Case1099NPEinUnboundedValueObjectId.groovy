package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification


class Case1099NPEinUnboundedValueObjectId extends Specification {

    class E {
        String profile
        List<String> roles

        E(String profile, List<String> roles) {
            this.profile = profile
            this.roles = roles
        }
    }

    class A {
          Map<String, List<E>> map
          List<E> list
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

        def a = new A(map: map, list: [new E("p1", ["teacher", "director"])])

        when:
        javers.commit("a", a)
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(A).build())

        def mapSnapshot = snapshots[0].getPropertyValue('map')

        then:
        mapSnapshot['key1'][0].value().endsWith('Case1099NPEinUnboundedValueObjectId$A/#map/key1/0')
        mapSnapshot['key1'][1].value().endsWith('Case1099NPEinUnboundedValueObjectId$A/#map/key1/1')
        mapSnapshot['key2'][0].value().endsWith('Case1099NPEinUnboundedValueObjectId$A/#map/key2/0')

        when:
        def voSnapshots = javers.findSnapshots(QueryBuilder.byClass(E).build())

        then:
        voSnapshots.size() == 4
        def e1Snapshot = voSnapshots.find{it.globalId.value().endsWith('Case1099NPEinUnboundedValueObjectId$A/#map/key1/0')}
        e1Snapshot.getPropertyValue('profile') == 'p1'
    }
}
