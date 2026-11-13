package org.javers.core

import jakarta.persistence.Id
import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.commit.CommitAssert
import org.javers.core.metamodel.annotation.Cascading
import org.javers.core.metamodel.clazz.EntityDefinitionBuilder
import org.javers.core.model.*
import org.javers.repository.jql.InstanceIdDTO
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
import static org.javers.core.JaversBuilder.javers
import static org.javers.core.model.DummyUser.dummyUser
import static GlobalIdTestBuilder.instanceId
import static GlobalIdTestBuilder.valueObjectId

/**
 * @author bartosz walacik
 */
class JaversCommitE2ETest extends Specification {

    def "should not commit snapshot of ShallowReferenceType entities" () {
        given:
        def javers = javers().build()
        def reference = new ShallowPhone(1, "123", new CategoryC(1, "some"))
        def entity =  new SnapshotEntity(id:1,
                shallowPhone: reference,
                shallowPhones: [reference] as Set,
                shallowPhonesList: [reference],
                shallowPhonesMap: ["key": reference]
        )

        when:
        def commit = javers.commit("", entity)

        then:
        commit.snapshots.each { println it }
        commit.snapshots.size() == 1
    }

    def "should not commit snapshot of a reference when a property has @ShallowReference"() {
        given:
        def javers = javers().build()
        def entity =  new PhoneWithShallowCategory(id:1, shallowCategory:new CategoryC(1, "old shallow"))

        when:
        def commit = javers.commit("", entity)

        then:
        println commit.snapshots[0]

        commit.snapshots.size() == 1
    }

    def "should not commit snapshot of a reference when a property is shallow via EntityDefinition"() {
        given:
        def javers = javers().registerEntity(
                EntityDefinitionBuilder.entityDefinition(PhoneWithShallowCategory)
                        .withIdPropertyName("id")
                        .withShallowProperties(["deepCategory"])
                        .build())
                .build()
        def entity =  new PhoneWithShallowCategory(id:1, deepCategory:new CategoryC(1, "old shallow"))

        when:
        def commit = javers.commit("", entity)

        then:
        println commit.snapshots[0]

        commit.snapshots.size() == 1
    }

    @Unroll
    def "should not commit snapshots in #collection when a property has @ShallowReference" () {
        given:
        def javers = javers().build()

        when:
        def commit = javers.commit("", entity)

        then:
        println commit.snapshots[0]

        commit.snapshots.size() == 1

        where:
        collection << ["Set", "List", "Map"]
        entity << [
            new PhoneWithShallowCategory(id:1, shallowCategories:[new CategoryC(1, "old shallow")] as Set),
            new PhoneWithShallowCategory(id:1, shallowCategoriesList:[new CategoryC(1, "old shallow")]),
            new PhoneWithShallowCategory(id:1, shallowCategoryMap:["foo":new CategoryC(1, "old shallow")])
        ]
    }

    def "should mark changed properties"() {
        given:
        def javers = javers().build()
        def entity = new SnapshotEntity(id:1, intProperty:4)

        when:
        javers.commit("author",entity)
        entity.dob = LocalDate.now()
        entity.intProperty = 5
        def commit = javers.commit("author",entity)

        then:
        commit.snapshots[0].changed as Set == ["dob","intProperty"] as Set
    }

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
                .hasId("2.00")
                .hasChanges(1)
                .hasObjectRemoved(instanceId(1,SnapshotEntity))
                .hasSnapshots(1)
                .hasTerminalSnapshot(instanceId(1,SnapshotEntity))

        where:
        deleteMethod << [ { j -> j.commitShallowDelete("some.login", new SnapshotEntity(id:1)) },
                          { j -> j.commitShallowDeleteById("some.login", InstanceIdDTO.instanceId(1,SnapshotEntity))} ]
        opType << ["using object instance","using globalId"]
    }

    def "should create terminal commits for the deleted object and its cascading children when using commitCascadingDelete"() {
        given:
        def javers = javers().build()
        def grandchild = new SnapshotEntity(id: 3)
        def child = new SnapshotEntity(id: 2, cascadingRef: grandchild)
        def nonCascadingChild = new SnapshotEntity(id: 4)
        def root = new SnapshotEntity(id: 1, cascadingRef: child, entityRef: nonCascadingChild)
        javers.commit("some.login", root)

        when:
        def commit = javers.commitCascadingDelete("some.login", new SnapshotEntity(id: 1))

        then:
        CommitAssert.assertThat(commit)
                .hasChanges(3)
                .hasObjectRemoved(instanceId(1, SnapshotEntity))
                .hasObjectRemoved(instanceId(2, SnapshotEntity))
                .hasObjectRemoved(instanceId(3, SnapshotEntity))
                .hasSnapshots(3)
                .hasTerminalSnapshot(instanceId(1, SnapshotEntity))
                .hasTerminalSnapshot(instanceId(2, SnapshotEntity))
                .hasTerminalSnapshot(instanceId(3, SnapshotEntity))

        javers.getLatestSnapshot(4, SnapshotEntity).get().terminal == false
    }

    def "should create terminal commits for the deleted object and its cascading children when using commitCascadingDeleteById"() {
        given:
        def javers = javers().build()
        def grandchild = new SnapshotEntity(id: 3)
        def child = new SnapshotEntity(id: 2, cascadingRef: grandchild)
        def nonCascadingChild = new SnapshotEntity(id: 4)
        def root = new SnapshotEntity(id: 1, cascadingRef: child, entityRef: nonCascadingChild)
        javers.commit("some.login", root)

        when:
        def commit = javers.commitCascadingDeleteById("some.login", InstanceIdDTO.instanceId(1, SnapshotEntity))

        then:
        CommitAssert.assertThat(commit)
                .hasChanges(3)
                .hasObjectRemoved(instanceId(1, SnapshotEntity))
                .hasObjectRemoved(instanceId(2, SnapshotEntity))
                .hasObjectRemoved(instanceId(3, SnapshotEntity))
                .hasSnapshots(3)
                .hasTerminalSnapshot(instanceId(1, SnapshotEntity))
                .hasTerminalSnapshot(instanceId(2, SnapshotEntity))
                .hasTerminalSnapshot(instanceId(3, SnapshotEntity))

        javers.getLatestSnapshot(4, SnapshotEntity).get().terminal == false
    }

    def "should fall back to a shallow delete when cascading-deleting-by-id a never-committed object"() {
        given:
        def javers = javers().build()

        when:
        def commit = javers.commitCascadingDeleteById("author", InstanceIdDTO.instanceId(1, SnapshotEntity))

        then:
        CommitAssert.assertThat(commit)
                .hasChanges(1)
                .hasObjectRemoved(instanceId(1, SnapshotEntity))
                .hasSnapshots(1)
    }

    def "commitCascadingDeleteById() should pass through the given commitProperties"() {
        given:
        def javers = javers().build()
        javers.commit("author", new SnapshotEntity(id: 1))

        when:
        def commit = javers.commitCascadingDeleteById("author", InstanceIdDTO.instanceId(1, SnapshotEntity), ["key": "ok"])

        then:
        commit.properties == ["key": "ok"]
    }

    def "should fall back to a shallow delete when cascading-deleting a never-committed object"() {
        given:
        def javers = javers().build()

        when:
        def commit = javers.commitCascadingDelete("author", new SnapshotEntity(id: 1))

        then:
        // Cascading traversal relies on reading the previous snapshot from the repository.
        // When the object was never committed, there is nothing to traverse, so this silently
        // behaves just like commitShallowDelete.
        CommitAssert.assertThat(commit)
                .hasChanges(1)
                .hasObjectRemoved(instanceId(1, SnapshotEntity))
                .hasSnapshots(1)
    }

    def "should cascade delete through a List of @Cascading children"() {
        given:
        def javers = javers().build()
        def child1 = new SnapshotEntity(id: 2)
        def child2 = new SnapshotEntity(id: 3)
        def root = new SnapshotEntity(id: 1, cascadingRefs: [child1, child2])
        javers.commit("author", root)

        when:
        def commit = javers.commitCascadingDelete("author", new SnapshotEntity(id: 1))

        then:
        CommitAssert.assertThat(commit)
                .hasChanges(3)
                .hasObjectRemoved(instanceId(1, SnapshotEntity))
                .hasObjectRemoved(instanceId(2, SnapshotEntity))
                .hasObjectRemoved(instanceId(3, SnapshotEntity))
    }

    def "should handle cyclic cascading references without an infinite loop"() {
        given:
        def javers = javers().build()
        def a = new SnapshotEntity(id: 1)
        def b = new SnapshotEntity(id: 2, cascadingRef: a)
        a.cascadingRef = b
        javers.commit("author", a)

        when:
        def commit = javers.commitCascadingDelete("author", new SnapshotEntity(id: 1))

        then:
        CommitAssert.assertThat(commit)
                .hasChanges(2)
                .hasObjectRemoved(instanceId(1, SnapshotEntity))
                .hasObjectRemoved(instanceId(2, SnapshotEntity))
    }

    def "should cascade-delete a child shared by two independent parents (documents current all-or-nothing behavior)"() {
        given:
        def javers = javers().build()
        def sharedChild = new SnapshotEntity(id: 3)
        def parentA = new SnapshotEntity(id: 1, cascadingRef: sharedChild)
        def parentB = new SnapshotEntity(id: 2, cascadingRef: sharedChild)
        javers.commit("author", parentA)
        javers.commit("author", parentB)

        when:
        javers.commitCascadingDelete("author", new SnapshotEntity(id: 1))

        then:
        // KNOWN LIMITATION: sharedChild is deleted even though parentB is still alive
        // and still references it. Cascading delete doesn't check for other owners.
        javers.getLatestSnapshot(3, SnapshotEntity).get().terminal == true
        javers.getLatestSnapshot(2, SnapshotEntity).get().terminal == false
    }

    def "should create a redundant terminal snapshot when cascading into an already-terminal descendant"() {
        given:
        def javers = javers().build()
        def child = new SnapshotEntity(id: 2)
        def root = new SnapshotEntity(id: 1, cascadingRef: child)
        javers.commit("author", root)
        javers.commitCascadingDelete("author", new SnapshotEntity(id: 1))
        def childHistorySizeAfterFirstDelete =
                javers.findSnapshots(QueryBuilder.byInstanceId(2, SnapshotEntity).build()).size()

        when:
        javers.commit("author", new SnapshotEntity(id: 1, cascadingRef: child))
        def commit = javers.commitCascadingDelete("author", new SnapshotEntity(id: 1))

        then:
        // KNOWN LIMITATION: child(2) was already terminal, but it gets a second,
        // redundant terminal snapshot/ObjectRemoved change instead of being skipped.
        CommitAssert.assertThat(commit)
                .hasObjectRemoved(instanceId(2, SnapshotEntity))
        javers.findSnapshots(QueryBuilder.byInstanceId(2, SnapshotEntity).build()).size() >
                childHistorySizeAfterFirstDelete
    }

    def "should cascade into ValueObjects but not Entities under CascadingMode.VALUE_OBJECTS fallback"() {
        given:
        def javers = javers().withCascadingMode(CascadingMode.VALUE_OBJECTS).build()
        def entityChild = new SnapshotEntity(id: 2)
        def root = new SnapshotEntity(id: 1, entityRef: entityChild, valueObjectRef: new DummyAddress("London"))
        javers.commit("author", root)

        when:
        def commit = javers.commitCascadingDelete("author", new SnapshotEntity(id: 1))

        then:
        def voId = valueObjectId(1, SnapshotEntity, "valueObjectRef")
        CommitAssert.assertThat(commit)
                .hasChanges(2)
                .hasObjectRemoved(instanceId(1, SnapshotEntity))
                .hasObjectRemoved(voId)
        javers.getLatestSnapshot(2, SnapshotEntity).get().terminal == false
    }

    def "should cascade into Entities under CascadingMode.ALL fallback"() {
        given:
        def javers = javers().withCascadingMode(CascadingMode.ALL).build()
        def entityChild = new SnapshotEntity(id: 2)
        def root = new SnapshotEntity(id: 1, entityRef: entityChild)
        javers.commit("author", root)

        when:
        def commit = javers.commitCascadingDelete("author", new SnapshotEntity(id: 1))

        then:
        CommitAssert.assertThat(commit)
                .hasChanges(2)
                .hasObjectRemoved(instanceId(1, SnapshotEntity))
                .hasObjectRemoved(instanceId(2, SnapshotEntity))
    }

    def "should respect @Cascading(false) override even when CascadingMode is ALL"() {
        given:
        def javers = javers().withCascadingMode(CascadingMode.ALL).build()
        def child = new SnapshotEntity(id: 10)
        def parent = new CascadingOverrideParent(id: 1, child: child)
        javers.commit("author", parent)

        when:
        javers.commitCascadingDelete("author", new CascadingOverrideParent(id: 1))

        then:
        javers.getLatestSnapshot(10, SnapshotEntity).get().terminal == false
    }

    def "should cascade through a @ShallowReference property under CascadingMode.ALL (documents current behavior)"() {
        given:
        def javers = javers().withCascadingMode(CascadingMode.ALL).build()
        def phone = new PhoneWithShallowCategory(id: 1L, shallowCategory: new CategoryC(2L, "old"))
        javers.commit("author", phone)

        when:
        javers.commitCascadingDelete("author", new PhoneWithShallowCategory(id: 1L))

        then:
        // KNOWN LIMITATION: @ShallowReference is meant to exclude a property from deep graph
        // traversal, but the cascading fallback mode doesn't take it into account. A terminal
        // snapshot is created for CategoryC(2) even though it never had a snapshot of its own.
        javers.getLatestSnapshot(2L, CategoryC).get().terminal == true
    }

    static class CascadingOverrideParent {
        @Id
        int id

        @Cascading(false)
        SnapshotEntity child
    }

    def "should create terminal commit when deleting non existing object"() {
        given:
        def javers = javers().build()
        def anEntity = new SnapshotEntity(id:1)

        when:
        def commit = javers.commitShallowDelete("some.login", anEntity)

        then:
        CommitAssert.assertThat(commit)
                .hasId("1.00")
                .hasObjectRemoved(instanceId(1,SnapshotEntity))
                .hasSnapshots(1)
                .hasTerminalSnapshot(instanceId(1,SnapshotEntity))
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
                    .hasId("1.00")
                    .hasSnapshots(2)
                    .hasSnapshot(cdoId,  [id:1, valueObjectRef:voId])
                    .hasSnapshot(voId,   [city : "London"])
                    .hasNewObject(cdoId)
                    .hasValueChangeAt('city', null, 'London')
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
        def javers = javers().withInitialChanges(false).build()
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
                    .hasChanges(2)
                    .hasNewObject(cdoId)
                    .hasValueChangeAt("intProperty", 2, 5)
    }

    def "should generate NewObject for added ValueObject"() {
        given:
        def javers = javers().build()
        def user = dummyUser().withDetails()
        javers.commit("some.login", user)

        when:
        user.withAddress("Tokyo")
        def commit = javers.commit("some.login", user)

        then:
        def voId = valueObjectId(1, DummyUserDetails, "dummyAddress")
        CommitAssert.assertThat(commit)
                    .hasSnapshots(2)
                    .hasSnapshot(instanceId(1, DummyUserDetails),[id:1,dummyAddress:voId,addressList:[],integerList:[]])
                    .hasSnapshot(voId,[city:"Tokyo"])
                    .hasChanges(1)
                    .hasValueChangeAt("city", null, "Tokyo")
    }

    def "should record terminal snapshots for removed ValueObject"() {
        given:
        def javers = javers().build()
        def user = dummyUser().withDetails(5).withAddress("Tokyo")
        javers.commit("some.login", user)

        when:
        user.dummyUserDetails.dummyAddress = null
        def commit = javers.commit("some.login", user)

        then:
        CommitAssert.assertThat(commit)
                    .hasSnapshots(2)
                    .hasSnapshot(instanceId(5, DummyUserDetails))
                    .hasTerminalSnapshot(valueObjectId(5, DummyUserDetails, "dummyAddress"))
                    .hasChanges(0)
    }

    def "should support new object added to List, deep in the graph"() {
        given:
        def javers = javers().build()
        def user = dummyUser().withDetails(5).withAddresses(new DummyAddress("London"),new DummyAddress("Paris"))
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
                    .hasChanges(2)
    }

    def "should support object removed from List, deep in the graph"() {
        given:
        def javers = javers().build()
        def user = dummyUser().withDetails(5).withAddresses(new DummyAddress("London"),new DummyAddress("Paris"))
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
