package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import org.javers.core.Javers
import org.javers.core.commit.CommitAssert
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.SnapshotEntity
import org.javers.core.snapshot.SnapshotsAssert
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId
import static org.javers.core.metamodel.object.ValueObjectId.ValueObjectIdDTO.valueObjectId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversCommitIntegrationTest extends Specification {

    Javers javers

    def setup() {
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db)
        javers = javers().registerJaversRepository(mongoRepository).build()

        mongoRepository.setJsonConverter(javers.getJsonConverter());
    }

    def "should store state history in JaversRepository1"() {
        given:
        def cdo = new SnapshotEntity(id: 1)
        javers.commit("author",cdo) //v. 1
        cdo.intProperty = 5
        javers.commit("author",cdo) //v. 2

        when:
        def snapshots = javers.getStateHistory(1, SnapshotEntity, 10)

        then:
        def cdoId = instanceId(1,SnapshotEntity)
        SnapshotsAssert.assertThat(snapshots)
                .hasSize(2)
                .hasSnapshot(cdoId, "1.0", [id:1])
                .hasSnapshot(cdoId, "2.0", [id:1, intProperty:5])
    }

    def "should store state history in JaversRepository"() {
        given:
        def ref = new SnapshotEntity(id:2)
        def cdo = new SnapshotEntity(id: 1, entityRef: ref)
        javers.commit("author",cdo) //v. 1
        ref.intProperty = 5
        javers.commit("author",cdo) //v. 2

        when:
        def snapshots = javers.getStateHistory(1, SnapshotEntity, 10)

        then:
        def cdoId = instanceId(2,SnapshotEntity)
        SnapshotsAssert.assertThat(snapshots)
                       .hasSize(2)
                       .hasSnapshot(cdoId, "1.0", [id:2])
                       .hasSnapshot(cdoId, "2.0", [id:2, intProperty:5])
    }


    def "should create initial commit when new objects"() {
        given:
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


    def "should compare property values with latest from repository"() {
        given:
        def user = dummyUser("John").withDetails(1).withAddress("London").build()
        javers.commit("some.login", user)

        when:
        user.setAge(10)
        user.dummyUserDetails.dummyAddress.city = "Paris"
        def commit = javers.commit("some.login", user)

        then:
        def voId = valueObjectId(1, DummyUserDetails, "dummyAddress")
        CommitAssert.assertThat(commit)
                    .hasSnapshots(2)
                    .hasChanges(2)
                    .hasSnapshot(instanceId("John",DummyUser), [name:"John", age:10, dummyUserDetails:instanceId(1,DummyUserDetails)])
                    .hasSnapshot(voId,[city:"Paris"])
                    .hasValueChangeAt("city", "London", "Paris")
                    .hasValueChangeAt("age", 0, 10)
    }

    def "should support new object reference, deep in the graph"() {
        given:
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

    def "should create empty commit when nothing changed"() {
        given:
        def cdo = new SnapshotEntity(listOfEntities:    [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def firstCommit = javers.commit("author",cdo)

        when:
        def secondCommit = javers.commit("author",cdo)

        then:
        firstCommit.snapshots.size() == 3
        !secondCommit.snapshots
        !secondCommit.diff.changes
    }

}
