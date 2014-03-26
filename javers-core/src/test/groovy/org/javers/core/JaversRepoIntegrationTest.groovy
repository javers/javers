package org.javers.core

import org.javers.core.diff.Diff
import org.javers.core.diff.DiffAssert
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import spock.lang.Ignore
import spock.lang.Specification

import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversRepoIntegrationTest extends Specification {

    @Ignore
    def "should create initial diff when new objects"() {
        given:
        Javers javers = JaversTestBuilder.javers()
        DummyUser newUser = initUserState()

        when:
        Diff diff = javers.commit("some.login", newUser)

        then:
        diff.author == "some.login"
        DiffAssert.assertThat(diff).has(3, NewObject).hasSize(3)
    }

    @Ignore
    def "should compare with latest from repository"() {
        given:
        Javers javers = JaversTestBuilder.javers()
        DummyUser user = initUserState()
        javers.commit("some.login", user)

        when:
        user.setAge(10)
        user.dummyUserDetails.someValue = "some"
        Diff diff = javers.commit("some.login", user)

        then:
        DiffAssert.assertThat(diff).has(2, ValueChange).hasSize(2)
    }

    private DummyUser initUserState() {
        DummyUser user = dummyUser("kazik").withDetails(1).build()
        user.dummyUserDetails.dummyAddress = new DummyAddress("aa","bb")
    }
}
