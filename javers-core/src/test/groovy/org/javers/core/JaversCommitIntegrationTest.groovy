package org.javers.core

import org.javers.core.commit.CommitAssert
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ReferenceAdded
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ReferenceRemoved
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.SnapshotEntity
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId
import static org.javers.core.metamodel.object.ValueObjectId.ValueObjectIdDTO.valueObjectId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversCommitIntegrationTest extends Specification {

    def "should create initial commit when new objects"() {
        given:
        def javers = javers().build()
        def newUser = dummyUser().withDetails().withAddress("London").build()

        when:
        def commit = javers.commit("some.login", newUser)

        then:
        commit.author == "some.login"
        commit.commitDate
        CommitAssert.assertThat(commit)
                    .hasSnapshots(3)
                    .hasId("1.0")
                    .hasChanges(3)
                    .hasChanges(3, NewObject)
    }

    def "should detect changes on ref node even if root is new"() {
        given:
        def oldRef = new SnapshotEntity(id: 2)
        def javers = javers().build()
        javers.commit("user",oldRef)

        def cdo = new SnapshotEntity(id: 1, entityRef: oldRef)

        when:
        def commit = javers.commit("user",cdo)

        then:
        def cdoId    = instanceId(1, SnapshotEntity)
        def oldRefId = instanceId(2, SnapshotEntity)
        CommitAssert.assertThat(commit)
                    .hasSnapshots(1)
                    .hasChanges(1)
                    .hasSnapshotWithValue(cdoId,"entityRef",oldRefId)
                    .hasNewObject(cdoId)
    }


    def "should compare property values with latest from repository"() {
        given:
        def javers = javers().build()
        def user = dummyUser("John").withDetails(1).withAddress("London").build()
        javers.commit("some.login", user)

        when:
        user.setAge(10)
        user.dummyUserDetails.dummyAddress.city = "Paris"
        def commit = javers.commit("some.login", user)

        then:
        CommitAssert.assertThat(commit)
                    .hasChanges(2)
                    .hasSnapshots(2)
                    .hasSnapshot(instanceId("John",DummyUser))
                    .hasSnapshot(valueObjectId(1, DummyUserDetails, "dummyAddress"))
                    .hasValueChangeAt("city", "London", "Paris")
                    .hasValueChangeAt("age", 0, 10)
    }

    def "should support new object reference, deep in the graph"() {
        given:
        def javers = javers().build()
        DummyUser user = dummyUser().withDetails(1).build()
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.dummyAddress = new DummyAddress("Tokyo")
        def commit = javers.commit("some.login", user)

        then:
        CommitAssert.assertThat(commit)
                    .hasChanges(2)
                    .hasSnapshots(2)
                    .hasSnapshot(instanceId(1, DummyUserDetails))
                    .hasSnapshot(valueObjectId(1, DummyUserDetails, "dummyAddress"))
                    .hasNewObject(valueObjectId(1, DummyUserDetails, "dummyAddress"))
                    .hasChanges(1, ReferenceChange)
    }

    def "should support removed reference, deep in the graph"() {
        given:
        def javers = javers().build()
        DummyUser user = dummyUser().withDetails(5).withAddress("Tokyo").build()
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.dummyAddress = null
        def commit = javers.commit("some.login", user)

        then:
        CommitAssert.assertThat(commit)
                    .hasChanges(2)
                    .hasSnapshots(1)
                    .hasSnapshot(instanceId(5, DummyUserDetails))
                    .hasObjectRemoved(valueObjectId(1, DummyUserDetails, "dummyAddress"))
                    .hasChanges(1,ReferenceChange)
    }

    def "should support new object added to List, deep in the graph"() {
        given:
        def javers = javers().build()
        DummyUser user = dummyUser().withDetails(5).withAddresses(new DummyAddress("London"),new DummyAddress("Paris")).build()
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.addressList.add(new DummyAddress("Tokyo"))
        def commit = javers.commit("some.login", user)

        then:
        def addedVoId = valueObjectId(1, DummyUserDetails, "addressList/2")
        CommitAssert.assertThat(commit)
                    .hasChanges(2)
                    .hasChanges(1, ReferenceAdded)
                    .hasNewObject(addedVoId)
                    .hasSnapshots(2)
                    .hasSnapshot(instanceId(5, DummyUserDetails))
                    .hasSnapshot(addedVoId)
    }

    def "should support object removed from List, deep in the graph"() {
        given:
        def javers = javers().build()
        DummyUser user = dummyUser().withDetails(5).withAddresses(new DummyAddress("London"),new DummyAddress("Paris")).build()
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.addressList = [new DummyAddress("London")]
        def commit = javers.commit("some.login", user)

        then:
        def removedVoId = valueObjectId(1, DummyUserDetails, "addressList/1")
        CommitAssert.assertThat(commit)
                    .hasChanges(2)
                    .hasChanges(1, ReferenceRemoved)
                    .hasObjectRemoved(removedVoId)
                    .hasSnapshots(1)
                    .hasSnapshot(instanceId(5, DummyUserDetails))
    }

    def "should create empty commit when nothing changed"() {
        given:
        def javers = javers().build()
        def cdo = new SnapshotEntity(listOfEntities:    [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def firstCommit = javers.commit("author",cdo)

        when:
        def secondCommit = javers.commit("author",cdo)

        then:
        firstCommit.snapshots.size() == 3
        !secondCommit.snapshots
        !secondCommit.diff.changes
    }

    /*
    def "should list all snapshots for given object"() {
        given:

        when:

        then:
    }*/
}
