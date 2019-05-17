package org.javers.core.snapshot

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static org.javers.core.GlobalIdTestBuilder.instanceId
import static org.javers.core.JaversBuilder.javers
import static org.javers.core.model.DummyUser.dummyUser

/**
 * @author bartosz walacik
 */
class SnapshotDifferIntegrationTest extends Specification {

    def "shouldn't add NewObject changes to change history by default"() {
        given:
        def javers = javers().build()
        javers.commit("some.login", new DummyAddress("London"))   //initial commit
        javers.commit("some.login", new DummyAddress("London 1")) //change commit

        when:
        def changes = javers.findChanges(
            QueryBuilder.byClass(DummyAddress).build())

        then:
        changes.size() == 1
        changes[0] instanceof ValueChange
    }

    def "should add NewObject changes to change history for initial commit when required"() {
        given:
        def javers = javers().build()
        def user = new DummyUser("kaz")
        javers.commit("some.login", user) //initial commit

        when:
        def changes = javers.findChanges(QueryBuilder.byInstanceId("kaz",DummyUser).withNewObjectChanges(true).build())

        then:
        changes.size() == 2
        changes[0] instanceof ValueChange //initial value
        changes[0].left == null
        changes[0].right == "kaz"
        changes[1] instanceof NewObject
        changes.each {
            assert it.affectedGlobalId == instanceId("kaz",DummyUser)
            assert it.commitMetadata.get().id.majorId == 1
        }
    }

    def "should add ObjectRemoved to change history for terminal commit"() {
        given:
        def javers = javers().build()
        def user = new DummyUser("kaz")
        javers.commit("some.login", user)
        javers.commitShallowDelete("some.login", user)

        when:
        def changes = javers.findChanges(QueryBuilder.byInstanceId("kaz",DummyUser).build())

        then:
        changes.size() == 1
        changes[0] instanceof ObjectRemoved
        changes[0].affectedGlobalId == instanceId("kaz",DummyUser)
        changes[0].commitMetadata.get().id.majorId == 2
    }

    @Unroll
    def "should recreate #expectedChangeType.simpleName from two persisted snapshots"() {
        given:
        def javers = javers().build()
        javers.commit("some.login", oldCdo)
        javers.commit("some.login", newCdo)

        when:
        def changes = javers.findChanges(QueryBuilder.byInstanceId("kaz",DummyUser).build())

        then:
        def change = changes[0]
        expectedChangeType.isInstance(change)
        change.affectedGlobalId == instanceId("kaz",DummyUser)
        change.propertyName == expectedChangedProperty
        change.left == expectedLeftValue
        change.right == expectedRightValue
        change.commitMetadata.get().commitDate
        change.commitMetadata.get().author == "some.login"
        change.commitMetadata.get().id.majorId == 2

        where:
        oldCdo <<  [dummyUser("kaz").withAge(5), dummyUser("kaz").withDetails(1)]
        newCdo <<  [dummyUser("kaz").withAge(6), dummyUser("kaz").withDetails(2)]
        expectedChangeType << [ValueChange, ReferenceChange]
        expectedChangedProperty << ["age","dummyUserDetails"]
        expectedLeftValue <<  [5, instanceId(1,DummyUserDetails)]
        expectedRightValue << [6, instanceId(2,DummyUserDetails)]
    }

    @Unroll
    def "should recreate ListChange ElementValueChange for #propertyName"() {
        given:
        def javers = javers().build()
        javers.commit("some.login", oldCdo)
        javers.commit("some.login", newCdo)

        when:
        def changes = javers.findChanges(QueryBuilder.byInstanceId(1,SnapshotEntity).build())

        then:
        def change = changes[0]
        change.affectedGlobalId == instanceId(1,SnapshotEntity)
        change.propertyName == propertyName
        change.changes.size() == 1
        def elementChange = change.changes[0]
        elementChange.leftValue == expectedLeftValue
        elementChange.rightValue == expectedRightValue
        elementChange.index == 0

        where:
        propertyType <<  ["Primitive", "Value", "Entity"]
        propertyName <<  ["listOfIntegers",  "listOfDates",  "listOfEntities"]
        oldCdo       <<  [new SnapshotEntity(listOfIntegers:      [1, 2]),
                          new SnapshotEntity(listOfDates:         [new LocalDate(2000, 1, 1), new LocalDate(2002, 1, 1)]),
                          new SnapshotEntity(listOfEntities:      [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
                         ]
        newCdo       <<  [new SnapshotEntity(listOfIntegers:      [5, 2]),
                          new SnapshotEntity(listOfDates:         [new LocalDate(2002, 2, 2), new LocalDate(2002, 1, 1)]),
                          new SnapshotEntity(listOfEntities:      [new SnapshotEntity(id:5), new SnapshotEntity(id:3)]),
                         ]
        expectedLeftValue <<  [1, new LocalDate(2000, 1, 1), instanceId(2,SnapshotEntity)]
        expectedRightValue << [5, new LocalDate(2002, 2, 2), instanceId(5,SnapshotEntity)]
    }

}
