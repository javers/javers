package org.javers.core.json.typeadapter

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.repository.jql.ValueObjectIdDTO
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.test.builder.DummyUserDetailsBuilder
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author pawel szymczyk
 */
class CdoSnapshotTypeAdapterTest extends Specification {

    def "should serialize CdoSnapshot to Json"() {
        given:
        def javers = javersTestAssembly()
        def dummyUser = new DummyUser(name:"kaz", age:5)
        def id = javers.idBuilder().instanceId(dummyUser)
        def snapshot = javers.snapshotFactory.createInitial(dummyUser, id, someCommitMetadata())

        when:
        def jsonText = javers.jsonConverter.toJson(snapshot)
        println jsonText

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.commitMetadata.id == 1.00
        json.commitMetadata.author == "kazik"
        //TODO date assertion
        json.commitMetadata.commitDate
        json.changedProperties == ["name","age"]

        json.globalId.entity == "org.javers.core.model.DummyUser"
        json.globalId.cdoId == "kaz"
        json.type == "INITIAL"
    }

    def "should serialize state with primitive values in CdoSnapshot"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz", DummyUser)

        def dummyUser = dummyUser("kaz")
                .withAge(1)
                .withFlag(true)
                .withCharacter('a' as char)
                .build()

        def snapshot = javers.snapshotFactory.createInitial(dummyUser, id, someCommitMetadata())

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)

        then:
        def json = new JsonSlurper().parseText(jsonText)

        with(json.state) {
            name == "kaz"
            age == 1
            flag == true
            _char == 'a' as char
        }
    }

    def "should serialize state with entity in CdoSnapshot"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz", DummyUser)

        def dummyUser = dummyUser("kaz")
                .withDetails()
                .build()

        def snapshot = javers.snapshotFactory.createInitial(dummyUser, id, someCommitMetadata())

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.state.dummyUserDetails.entity == "org.javers.core.model.DummyUserDetails"
    }

    def "should serialize state with value object in CdoSnapshot"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId(1, DummyUserDetails)

        def dummyUserDetails = DummyUserDetailsBuilder.dummyUserDetails(1).withAddress("London", "St John Street").build()

        def snapshot = javers.snapshotFactory.createInitial(dummyUserDetails, id, someCommitMetadata())

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.state.dummyAddress.valueObject == "org.javers.core.model.DummyAddress"
    }

    def "should serialize state with collections in CdoSnapshots"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz", DummyUser)

        def dummyUser = dummyUser("kaz")
                .withIntArray(1, 2)
                .withIntegerList(3, 4)
                .withStringsSet("5", "6")
                .withPrimitiveMap([time: new LocalDateTime(2000, 1, 1, 12, 0)])
                .build()

        def snapshot = javers.snapshotFactory.createInitial(dummyUser, id, someCommitMetadata())

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)

        then:
        def json = new JsonSlurper().parseText jsonText

        with (json.state) {
            intArray == [1, 2]
            integerList == [3, 4]
            stringSet == ["5", "6"]
            primitiveMap.time == "2000-01-01T12:00:00"
        }
    }

    def "should deserialize CdoSnapshot"() {

        given:
        def changed = ["name", "age"]
        def json = new JsonBuilder()
        json {
            commitMetadata {
                id "1.0"
                author "author"
                dateTime "2000-01-01T12:00:00"
            }
            globalId {
                entity "org.javers.core.model.DummyUser"
                cdoId "kaz"
            }
            type "INITIAL"
            state {
            }
            changedProperties changed
        }
        println json.toString()

        when:
        def snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        snapshot.commitMetadata.id.value() == "1.0"
        snapshot.globalId == instanceId("kaz",DummyUser)
        snapshot.initial == true
        snapshot.changed.collect{it.name} as Set == ["name", "age"] as Set
    }

    def "should deserialize CdoSnapshot state with primitive values"() {

        given:
        def json = new JsonBuilder()
        json {
            commitMetadata {
                commitId "1.0"
                author "author"
                dateTime "2000-01-01T12:00:00"
            }
            globalId {
                entity "org.javers.core.model.DummyUser"
                cdoId "kaz"
            }
            state {
                _char "a"
                name "kaz"
                age 1
                flag true
            }
        }

        when:
        def snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        with (snapshot) {
            getPropertyValue("_char") == 'a' as char
            getPropertyValue("name") == "kaz"
            getPropertyValue("age") == 1
            getPropertyValue("flag") == true
        }
    }

    def "should deserialize CdoSnapshot state with entity"() {

        given:
        def json = new JsonBuilder()
        json {
            commitMetadata {
                commitId "1.0"
                author "author"
                dateTime "2000-01-01T12:00:00"
            }
            globalId {
                entity "org.javers.core.model.DummyUser"
                cdoId "kaz"
            }
            state {
                name "kaz"
                dummyUserDetails {
                    entity "org.javers.core.model.DummyUserDetails"
                    cdoId 1
                }
            }
        }

        when:
        def snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        def entityId = snapshot.getPropertyValue("dummyUserDetails")
        entityId == instanceId(1, DummyUserDetails)
    }

    def "should deserialize CdoSnapshot state with valueObject"() {

        given:
        def json = new JsonBuilder()
        json {
            commitMetadata {
                commitId "1.0"
                author "author"
                dateTime "2000-01-01T12:00:00"
            }
            globalId {
                entity "org.javers.core.model.DummyUserDetails"
                cdoId 1
            }
            state {
                dummyAddress {
                    valueObject "org.javers.core.model.DummyAddress"
                    ownerId {
                        entity "org.javers.core.model.DummyUserDetails"
                        cdoId 1
                    }
                    fragment "dummyAddress"
                }
                id 1
            }
        }

        when:
        def snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        def valueObjectId = snapshot.getPropertyValue("dummyAddress")
        valueObjectId == ValueObjectIdDTO.valueObjectId(1, DummyUserDetails, "dummyAddress")
    }

    def "should deserialize CdoSnapshot state with collections"() {

        given:
        def json = new JsonBuilder()
        def ids = [1, 2]
        json {
            commitMetadata {
                commitId "1.0"
                author "author"
                dateTime "2000-01-01T12:00:00"
            }
            globalId {
                entity "org.javers.core.model.DummyUser"
                cdoId "kaz"
            }
            state {
                intArray 1, 2
                integerList 3, 4
                stringSet "5", "6"
                primitiveMap {
                    time "2000-01-01T12:00:00"
                }
                dummyUserDetailsList ids.collect {
                        id -> [entity: "org.javers.core.model.DummyUserDetails", cdoId: id]
                }
                dateTimes "2000-01-01T12:00:00", "2000-01-01T12:00:00"
                name "kaz"
            }
        }

        when:
        def snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        with (snapshot) {
            getPropertyValue("intArray") == [1, 2].toArray()
            getPropertyValue("integerList") == [3, 4]
            getPropertyValue("stringSet") == ["5", "6"] as Set
            getPropertyValue("primitiveMap") == [time: new LocalDateTime(2000, 1, 1, 12, 0)]

            getPropertyValue("dummyUserDetailsList").size() == 2
            getPropertyValue("dummyUserDetailsList").get(0) == instanceId(1, DummyUserDetails)
            getPropertyValue("dummyUserDetailsList").get(1) == instanceId(2, DummyUserDetails)

            getPropertyValue("dateTimes").size() == 2
            getPropertyValue("dateTimes")[0] instanceof LocalDateTime
        }
    }

    CommitMetadata someCommitMetadata(){
        new CommitMetadata("kazik", LocalDateTime.now(), new CommitId(1, 0))
    }
}