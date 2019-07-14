package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.ValueObject
import org.javers.repository.jql.QueryBuilder
import org.junit.Test
import spock.lang.Specification

import javax.persistence.Id

/**
 * @author adriano.machado
 */
class Case779SortedSets extends Specification {

    static class MasterWithSet {
        @Id
        String id

        Set<Detail> set
    }

    static class MasterWithSortedSet {
        @Id
        String id

        SortedSet<Detail> sortedSet
    }

    @ValueObject
    static class Detail {
        String data

        Detail(String data) {
            this.data = data
        }
    }

    @Test
    def "should work with objects using Sets"() {
        given:
        Javers javers = JaversBuilder.javers().build()

        MasterWithSet master = new MasterWithSet()
        master.id = "X"
        master.set = [new Detail("data 2"), new Detail("data 1")] as Set

        javers.commit("anonymous", master)

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(MasterWithSet).build())

        then:
        snapshots
    }

    @Test
    def "should work with objects using SortedSets"() {
        given:
        Javers javers = JaversBuilder.javers().build()

        MasterWithSortedSet master = new MasterWithSortedSet()
        master.id = "X"
        master.sortedSet = new TreeSet<>(Comparator.comparing{o -> o.data})

        master.sortedSet.add(new Detail("data 2"))
        master.sortedSet.add(new Detail("data 1"))

        javers.commit("anonymous", master)

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(MasterWithSortedSet).build())

        then:
        snapshots
    }
}
