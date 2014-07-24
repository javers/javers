package org.javers.core

import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.ElementValueChange
import org.javers.core.diff.changetype.container.ListChange
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
class JaversSnapshotDifferIntegrationTest extends Specification {

    @Unroll
    def "should recreate #expectedChangeType.simpleName from two persisted snapshots"() {
        given:
        def javers = javers().build()
        javers.commit("some.login", oldCdo)
        javers.commit("some.login", newCdo)

        when:
        def changes = javers.getChangeHistory("kaz",DummyUser,10)

        then:
        changes.size() == 1
        changes[0].class == expectedChangeType
        def change = changes[0]
        change.affectedCdoId == instanceId("kaz",DummyUser)
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
        def changes = javers.getChangeHistory(1,SnapshotEntity,10)

        then:
        changes.size() == 1
        ListChange change = changes[0]
        change.affectedCdoId == instanceId(1,SnapshotEntity)
        change.property.name == propertyName
        change.changes.size() == 1
        ElementValueChange elementChange = change.changes[0]
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

    def "should return changes in reverse chrono order"() {
        given:
        def javers = javers().build()
        def user = dummyUser("kaz").build();

        (1..4).each {
            user.setAge(it)
            javers.commit("some.login", user)
        }

        when:
        def changes = javers.getChangeHistory("kaz",DummyUser,10)

        then:
        changes.size() == 3
        (0..2).each {
            ValueChange change = changes[it]
            assert change.left  == 4-it-1
            assert change.right == 4-it
        }
    }
}
