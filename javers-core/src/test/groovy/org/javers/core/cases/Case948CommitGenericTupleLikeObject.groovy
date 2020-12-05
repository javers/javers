package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.commit.Commit
import spock.lang.Specification

class Case948CommitGenericTupleLikeObject extends Specification {
    class Pair<L, R> {
        L left
        R right

        Pair(L left, R right) {
            this.left = left
            this.right = right
        }

        @Override
        String toString() {
            return "Pair [left=" + left + ", right=" + right + "]"
        }
    }

    def "should track changes when committing generic-tuple-like object"() {
        given:
        Javers javers = JaversBuilder.javers().build()

        def obj = new Pair(1L, "foo")

        when:
        Commit commit = javers.commit("jay", obj)

        obj.right = "bar"

        commit = javers.commit("jay", obj)

        println "commit.changes" + commit.changes.prettyPrint()

        then:
        commit.getChanges().size() == 1
    }
}

