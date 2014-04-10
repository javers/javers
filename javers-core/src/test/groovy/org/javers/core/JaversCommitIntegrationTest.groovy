package org.javers.core

import org.javers.core.diff.DiffAssert
import org.javers.core.diff.changetype.NewObject
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification
import static org.javers.core.JaversBuilder.javers
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversCommitIntegrationTest extends Specification {

    def "should create initial diff when new objects"() {
        given:
        def javers = javers().build()
        def newUser = dummyUser().withDetails().withAddress("London").build()

        when:
        def commit = javers.commit("some.login", newUser)

        then:
        commit.author == "some.login"
        commit.commitDate
        commit.snapshots.size() == 3
        DiffAssert.assertThat(commit.diff).has(3, NewObject)
    }

    def "should compare property values with latest from repository"() {
        given:
        def javers = javers().build()
        def user = dummyUser().withDetails().withAddress("London").build()
        javers.commit("some.login", user)

        when:
        user.setAge(10)
        user.dummyUserDetails.dummyAddress.city = "Paris"
        def commit = javers.commit("some.login", user)

        then:
        DiffAssert.assertThat(commit.diff)
                  .hasValueChangeAt("city","London","Paris")
                  .hasValueChangeAt("age",0,10)
    }

    def "should support new object reference, deep in the graph"() {
        given:
        def javers = javers().build()
        DummyUser user = dummyUser().withDetails().build()
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.dummyAddress = new DummyAddress("Tokyo")
        def commit = javers.commit("some.login", user)

        then:
        DiffAssert.assertThat(commit.diff)
                  .hasNewObject(javers.idBuilder().withOwner(1, DummyUserDetails).voId(DummyAddress, "dummyAddress"))
                  .hasValueChangeAt("city",null,"Tokyo")
    }

    def "should support removed reference, deep in the graph"() {
        given:
        def javers = javers().build()
        DummyUser user = dummyUser().withDetails().withAddress("Tokyo").build()
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.dummyAddress = null
        def commit = javers.commit("some.login", user)

        then:
        DiffAssert.assertThat(commit.diff)
                .hasObjectRemoved(javers.idBuilder().withOwner(1, DummyUserDetails).voId(DummyAddress, "dummyAddress"))
    }

    def "should support new object added to List, deep in the graph"() {
        given:
        def javers = javers().build()
        DummyUser user = dummyUser().withDetails().withAddresses(new DummyAddress("London"),new DummyAddress("Paris")).build()
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.addressList.add(new DummyAddress("Tokyo"))
        def commit = javers.commit("some.login", user)

        then:
        DiffAssert.assertThat(commit.diff)
                  .hasNewObject(javers.idBuilder().withOwner(1, DummyUserDetails).voId(DummyAddress, "addressList/2"))
                  .hasValueChangeAt("city",null,"Tokyo")
    }

    def "should support object removed from List, deep in the graph"() {
        given:
        def javers = javers().build()
        DummyUser user = dummyUser().withDetails().withAddresses(new DummyAddress("London"),new DummyAddress("Paris")).build()
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.addressList = [new DummyAddress("London")]
        def commit = javers.commit("some.login", user)

        then:
        DiffAssert.assertThat(commit.diff)
                   .hasObjectRemoved(javers.idBuilder().withOwner(1, DummyUserDetails).voId(DummyAddress, "addressList/1"))
    }
}
