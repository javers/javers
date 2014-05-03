package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.commit.CommitId
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails


class CdoSnapshotTypeAdapterTest extends Specification {

    def "should serialize commitId in CdoSnapshot"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz", DummyUser)
        def snapshot = javers.snapshotFactory.create(dummyUser("kaz").build(), id)

        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.commitId == "1.0"
    }

    def "should serialize globalCdoId in CdoSnapshot"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz", DummyUser)
        def snapshot = javers.snapshotFactory.create(dummyUser("kaz").build(), id)
        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.globalCdoId.entity == "org.javers.core.model.DummyUser"
        json.globalCdoId.cdoId == "kaz"
    }


    def "should serialize state with primitive values in CdoSnapshot"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz",DummyUser)

        def dummyUser = dummyUser("kaz")
                .withAge(1)
                .withFlag(true)
                .withCharacter('a' as char)
                .build()

        def snapshot = javers.snapshotFactory.create(dummyUser, id)
        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)

        json.state.name == "kaz"
        json.state.age == 1
        json.state.flag == true
        json.state._char == 'a' as char
    }

    def "should serialize state with entity in CdoSnapshot"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz",DummyUser)

        def dummyUser = dummyUser("kaz")
                .withDetails()
                .build()

        def snapshot = javers.snapshotFactory.create(dummyUser, id)
        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)

        json.state.dummyUserDetails.entity == "org.javers.core.model.DummyUserDetails"
    }

    def "should serialize state with value object in CdoSnapshot"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId(1,DummyUserDetails)

        def dummyUserDetails = dummyUserDetails(1).withAddress("London", "St John Street").build()

        def snapshot = javers.snapshotFactory.create(dummyUserDetails, id)
        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)

        json.state.dummyAddress.valueObject == "org.javers.core.model.DummyAddress"
    }

    def "should serialize state with collection in CdoSnapshots"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz",DummyUser)

        def dummyUser = dummyUser("kaz")
                .withIntArray(1, 2)
                .withIntegerList(3, 4)
                .withStringsSet("5", "6")
                .withPrimitiveMap([time : new LocalDateTime(2000, 1, 1, 12, 0)])
                .build()

        def snapshot = javers.snapshotFactory.create(dummyUser, id)
        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)

        json.state.intArray == [1, 2]
        json.state.integerList == [3, 4]
        json.state.stringSet == ["5", "6"]
        json.state.primitiveMap.time == "2000-01-01T12:00:00"
    }

    def "ToJson"() {

    }
}
