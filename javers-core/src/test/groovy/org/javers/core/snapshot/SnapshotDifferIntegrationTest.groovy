package org.javers.core.snapshot

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.SnapshotEntity
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class SnapshotDifferIntegrationTest extends Specification {

    def "shouldn't add NewObject to change history for ordinary commit"() {
        given:
        def javers = javers().build()
        def user = new DummyUser("kaz")
        javers.commit("some.login", user) //initial commit

        (1..2).each {
            user.setAge(it)
            javers.commit("some.login", user) //change commit
        }

        when:
        def changes = javers.getChangeHistory(instanceId("kaz",DummyUser),2)

        then:
        changes.size() == 2
        changes.each {
            it instanceof ValueChange
        }
    }

    def "should add NewObject to change history for initial commit"() {
        given:
        def javers = javers().build()
        def user = new DummyUser("kaz")
        javers.commit("some.login", user) //initial commit

        when:
        def changes = javers.getChangeHistory(instanceId("kaz",DummyUser),5)

        then:
        changes.size() == 1
        changes[0] instanceof NewObject

        when:
        user.setAge(18)
        javers.commit("some.login",user) //change commit
        changes = javers.getChangeHistory(instanceId("kaz",DummyUser),5)

        then:
        changes.size() == 2
        changes[0] instanceof ValueChange
        changes[1] instanceof NewObject
        changes[1].affectedGlobalId == instanceId("kaz",DummyUser)
        changes[1].commitMetadata.get().id.majorId == 1
    }

    def "should add ObjectRemoved to change history for terminal commit"() {
        given:
        def javers = javers().build()
        def user = new DummyUser("kaz")
        javers.commit("some.login", user)
        javers.commitDelete("some.login", user)

        when:
        def changes = javers.getChangeHistory(instanceId("kaz",DummyUser),5)

        then:
        changes.size() == 2
        changes[0] instanceof ObjectRemoved
        changes[0].affectedGlobalId == instanceId("kaz",DummyUser)
        changes[0].commitMetadata.get().id.majorId == 2
        changes[1] instanceof NewObject
    }

    @Unroll
    def "should recreate #expectedChangeType.simpleName from two persisted snapshots"() {
        given:
        def javers = javers().build()
        javers.commit("some.login", oldCdo)
        javers.commit("some.login", newCdo)

        when:
        def changes = javers.getChangeHistory(instanceId("kaz",DummyUser),10)

        then:
        changes.size() == 2
        def change = changes[0]
        change.class == expectedChangeType
        change.affectedGlobalId == instanceId("kaz",DummyUser)
        change.property.name == expectedChangedProperty
        change.left == expectedLeftValue
        change.right == expectedRightValue
        change.commitMetadata.get().commitDate
        change.commitMetadata.get().author == "some.login"
        change.commitMetadata.get().id  == "2.0"

        where:
        oldCdo <<  [dummyUser("kaz").withAge(5).build(), dummyUser("kaz").withDetails(1)]
        newCdo <<  [dummyUser("kaz").withAge(6).build(), dummyUser("kaz").withDetails(2)]
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
        def changes = javers.getChangeHistory(instanceId(1,SnapshotEntity),10)

        then:
        changes.size() == 2
        def change = changes[0]
        change.affectedGlobalId == instanceId(1,SnapshotEntity)
        change.property.name == propertyName
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

    def "should return changes in reverse chronological order"() {
        given:
        def javers = javers().build()
        def user = new DummyUser("kaz")

        (1..4).each {
            user.setAge(it)
            javers.commit("some.login", user)
        }

        when:
        def changes = javers.getChangeHistory(instanceId("kaz",DummyUser),5)

        then:
        changes.size() == 4
        (0..2).each {
            ValueChange change = changes[it]
            assert change.left  == 4-it-1
            assert change.right == 4-it
        }
        changes[3] instanceof NewObject
    }
}
