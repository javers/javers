package org.javers.core

import org.javers.core.commit.CommitAssert
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.SnapshotEntity
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId
import static org.javers.core.metamodel.object.ValueObjectIdDTO.valueObjectId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversCommitIntegrationTest extends Specification {

    def "should create initial commit when new objects"() {
        given:
        def javers = javers().build()
        def newUser = new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("London"))

        when:
        def commit = javers.commit("some.login", newUser)

        then:
        def cdoId = instanceId(1, SnapshotEntity)
        def voId  = valueObjectId(1, SnapshotEntity, "valueObjectRef")
        commit.author == "some.login"
        commit.commitDate
        CommitAssert.assertThat(commit)
                    .hasId("1.0")
                    .hasSnapshots(2)
                    .hasSnapshot(cdoId,  [id:1, valueObjectRef:voId])
                    .hasSnapshot(voId,   [city : "London"])
                    .hasNewObject(cdoId, [id:1, valueObjectRef:voId])
                    .hasNewObject(voId,  [city : "London"])
    }

    def "should detect reference change"() {
        given:
        def oldRef = new SnapshotEntity(id: 2)
        def cdo = new SnapshotEntity(id: 1, entityRef: oldRef)
        def javers = javers().build()
        javers.commit("user",cdo)

        when:
        def newRef = new SnapshotEntity(id: 5)
        cdo.entityRef = newRef
        def commit = javers.commit("user",cdo)

        then:
        CommitAssert.assertThat(commit)
                    .hasSnapshots(2)
                    .hasSnapshot(instanceId(5,SnapshotEntity),[id:5])
                    .hasSnapshot(instanceId(1,SnapshotEntity),[id:1,entityRef: instanceId(5,SnapshotEntity)])
                    .hasChanges(3)
                    .hasNewObject(instanceId(5,SnapshotEntity),[id:5])
                    .hasValueChangeAt("id",0,5)
                    .hasReferenceChangeAt("entityRef", instanceId(2,SnapshotEntity), instanceId(5,SnapshotEntity))
    }

    def "should detect changes on referenced node even if root is new"() {
        given:
        def oldRef = new SnapshotEntity(id: 2, intProperty:2)
        def javers = javers().build()
        javers.commit("user",oldRef)

        def cdo = new SnapshotEntity(id: 1, entityRef: oldRef)
        oldRef.intProperty = 5

        when:
        def commit = javers.commit("user",cdo)

        then:
        def cdoId    = instanceId(1, SnapshotEntity)
        def oldRefId = instanceId(2, SnapshotEntity)
        CommitAssert.assertThat(commit)
                    .hasSnapshots(2)
                    .hasSnapshot(cdoId,    [id:1, entityRef:oldRefId ])
                    .hasSnapshot(oldRefId, [id:2, intProperty:5])
                    .hasNewObject(cdoId,   [id:1, entityRef:oldRefId, ])
                    .hasValueChangeAt("intProperty", 2, 5)
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
        def voId = valueObjectId(1, DummyUserDetails, "dummyAddress")
        CommitAssert.assertThat(commit)
                    .hasSnapshots(2)
                    .hasSnapshot(instanceId(1, DummyUserDetails),[id:1,dummyAddress:voId,addressList:[],integerList:[]])
                    .hasSnapshot(voId,[city:"Tokyo"])
                    .hasNewObject(voId,[city:"Tokyo"])
                    .hasReferenceChangeAt("dummyAddress",null,voId)

    }

    //not sure about that.
    // We know that object was removed when concerning the local context of LiveGraph and ShadowGraph
    // but we don't know if it was removed 'globally'
    def "should generate only ReferenceChange for removed objects"() {
        given:
        def javers = javers().build()
        DummyUser user = dummyUser().withDetails(5).withAddress("Tokyo").build()
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.dummyAddress = null
        def commit = javers.commit("some.login", user)

        then:
        def voId = valueObjectId(5, DummyUserDetails, "dummyAddress")
        CommitAssert.assertThat(commit)
                    .hasSnapshots(1)
                    .hasSnapshot(instanceId(5, DummyUserDetails))
                    .hasChanges(1)
                    .hasReferenceChangeAt("dummyAddress",voId,null)
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
        def addedVoId = valueObjectId(5, DummyUserDetails, "addressList/2")
        CommitAssert.assertThat(commit)
                    .hasSnapshots(2)
                    .hasSnapshot(instanceId(5, DummyUserDetails))
                    .hasSnapshot(addedVoId)
                    .hasListReferenceAddedAt("addressList",addedVoId)
                    .hasNewObject(addedVoId,[city:"Tokyo"])
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
        def removedVoId = valueObjectId(5, DummyUserDetails, "addressList/1")
        CommitAssert.assertThat(commit)
                    .hasSnapshots(1)
                    .hasSnapshot(instanceId(5, DummyUserDetails))
                    .hasListReferenceRemovedAt("addressList",removedVoId)
    }
}
