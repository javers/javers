package org.javers.core.diff

import org.javers.core.Javers
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javers
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails


class InitialDiffTest extends Specification {

    def "should generate initial diff with new reference"() {
        given:
        Javers javers = javers()

        when:
        Diff diff = javers.initialDiff("kazik", dummyUser.build())

        then:
        with(diff) {
            it.hasChanges()
            changes.size() == changesSize
        }

        where:
        dummyUser                                                                                         || changesSize
//        dummyUser("zenek").withAge(1)                                                                     || 5
//        dummyUser("zenek").withDetails()                                                                  || 9
//        dummyUser("zenek").withSupervisor("marian")                                                       || 11
        dummyUser("zenek").withDetails(dummyUserDetails().withAddress("Wall Street", "New York").build()) || 13

    }

    def "test"() {
        given:
        Javers javers = javers()
        DummyUserDetails details = dummyUserDetails().withAddress().build()

        when:
        Diff diff = javers.initialDiff("kazik", details)

        then:
        diff.hasChanges()
    }


}