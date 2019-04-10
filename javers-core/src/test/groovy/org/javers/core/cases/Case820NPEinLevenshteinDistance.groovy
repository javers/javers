package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.ListCompareAlgorithm
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/820
 */
class Case820NPEinLevenshteinDistance extends Specification {

    def "should not fail when comparing list to null or empty list"(){
        given:
        Javers javers = JaversBuilder.javers().withNewObjectsSnapshot(true)
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build()


        when:
        def diff = javers.compare(null, new Obj(["test"]))

        then:
        diff.changes

        when:
        def diff2 = javers.compareCollections(Collections.emptyList(),
                                              Collections.singletonList(new Obj(["test"])), Obj)

        then:
        diff2.changes
    }

    class Obj {
        private List<String> strings

        Obj(List<String> strings) {
            this.strings = strings
        }
    }
}
