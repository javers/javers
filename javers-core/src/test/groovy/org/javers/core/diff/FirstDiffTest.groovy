package org.javers.core.diff

import org.javers.core.Javers
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javers
import static org.javers.test.builder.DummyUserBuilder.dummyUser


class FirstDiffTest extends Specification {

    def "should generate initial diff with one lvl graph"() {
        given:
        DummyUser right = dummyUser("zenon").withAge(1).build()
        Javers javers = javers()

        when:
        Diff diff = javers.firstDiff("kazik", right)

        then:
        with(diff) {
            it.hasChanges()
            changes.size() == 5
        }
    }

    def "should generate initial diff with new reference"() {
        given:
        DummyUser right = dummyUser("zenon").withDetails().build();
        Javers javers = javers()

        when:
        Diff diff = javers.firstDiff("kazik", right)

        then:
        with(diff) {
            it.hasChanges()
            changes.size() == 8
        }
    }

//    def "should compare graph with null"() {
//        given:
//        DummyUser right = dummyUser("zenon").withAge(1).build()
//        Javers javers = javers()
//
//        when:
//        Diff diff = javers.firstDiff("kazik", right)
//
//        then:
//        diff.hasChanges()
//        diff.changes.size() == 2
//    }
}