package org.javers.core

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.commit.CommitAssert
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.SnapshotEntity
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
import static org.javers.core.JaversBuilder.javers
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId
import static org.javers.core.metamodel.object.ValueObjectIdDTO.valueObjectId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversCommitE2ETest extends Specification {

    @Unroll
    def "should create terminal commit for removed object when #opType"() {
        given:
        def final javers = javers().build()
        def anEntity = new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2))
        javers.commit("some.login", anEntity)

        when:
        def commit = deleteMethod.call(javers)

        then:
        CommitAssert.assertThat(commit)
                .hasId("2.0")
                .hasChanges(1)
                .hasObjectRemoved(instanceId(1,SnapshotEntity))
                .hasSnapshots(1)
                .hasTerminalSnapshot(instanceId(1,SnapshotEntity))

        where:
        deleteMethod << [ { j -> j.commitShallowDelete("some.login", new SnapshotEntity(id:1)) },
                          { j -> j.commitShallowDeleteById("some.login", instanceId(1,SnapshotEntity))} ]
        opType << ["using object instance","using globalId"]
    }

    def "should fail when deleting non existing object"() {
        given:
        def javers = javers().build()
        def anEntity = new SnapshotEntity(id:1)

        when:
        javers.commitShallowDelete("some.login", anEntity)

        then:
        JaversException exception = thrown()
        exception.code == JaversExceptionCode.CANT_DELETE_OBJECT_NOT_FOUND
    }

    def "should create initial commit for new objects"() {
        given:
        def javers = javers().build()
        def anEntity = new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("London"))

        when:
        def commit = javers.commit("some.login", anEntity)

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
                    .hasNewObject(cdoId)
                    .hasNewObject(voId)
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
                    .hasChanges(2)
                    .hasNewObject(instanceId(5,SnapshotEntity))
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
                    .hasNewObject(cdoId)
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
                    .hasNewObject(voId)
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
                    .hasNewObject(addedVoId)
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

    def "should not support Map of <ValueObject,?>, no good idea how to handle this"() {
        given:
        def javers = javers().build()
        def cdo = new SnapshotEntity(mapVoToPrimitive:  [(new DummyAddress("London")):"this"])

        when:
        javers.commit("author", cdo)
        javers.commit("author", cdo)

        then:
        def e = thrown(JaversException)
        e.code == VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
    }

}
