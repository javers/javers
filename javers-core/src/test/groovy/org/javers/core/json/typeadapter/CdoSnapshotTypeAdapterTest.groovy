package org.javers.core.json.typeadapter

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.commit.CommitId
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.test.builder.DummyUserDetailsBuilder
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.test.builder.DummyUserBuilder.dummyUser


class CdoSnapshotTypeAdapterTest extends Specification {

    def "should serialize CdoSnapshot to Json"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz", DummyUser)
        def snapshot = javers.snapshotFactory.create(dummyUser().build(), id)

        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)
//        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.commitId == "1.0"

        json.globalCdoId.entity == "org.javers.core.model.DummyUser"
        json.globalCdoId.cdoId == "kaz"
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

        def snapshot = javers.snapshotFactory.create(dummyUser, id)
        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)
//        println(jsonText)

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
        def id = javers.idBuilder().instanceId("kaz", DummyUser)

        def dummyUser = dummyUser("kaz")
                .withDetails()
                .build()

        def snapshot = javers.snapshotFactory.create(dummyUser, id)
        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)
//        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)

        json.state.dummyUserDetails.entity == "org.javers.core.model.DummyUserDetails"
    }

    def "should serialize state with value object in CdoSnapshot"() {

        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId(1, DummyUserDetails)

        def dummyUserDetails = DummyUserDetailsBuilder.dummyUserDetails(1).withAddress("London", "St John Street").build()

        def snapshot = javers.snapshotFactory.create(dummyUserDetails, id)
        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)
//        println(jsonText)

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

        def snapshot = javers.snapshotFactory.create(dummyUser, id)
        snapshot.bindTo(new CommitId(1, 0))

        when:
        String jsonText = javers.jsonConverter.toJson(snapshot)
//        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)

        json.state.intArray == [1, 2]
        json.state.integerList == [3, 4]
        json.state.stringSet == ["5", "6"]
        json.state.primitiveMap.time == "2000-01-01T12:00:00"
    }

    def "should deserialize CdoSnapshot"() {

        given:
        def json = new JsonBuilder()
        json {
            commitId "1.0"
            globalCdoId {
                entity "org.javers.core.model.DummyUser"
                cdoId "kaz"
            }
            state {
            }
        }

        when:
//        println json.toPrettyString()
        CdoSnapshot snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        snapshot.commitId.value() == "1.0"
        snapshot.globalId.getCdoId() == "kaz"
        snapshot.globalId.getCdoClass().getSourceClass() == DummyUser
    }

    def "should deserialize state with primitive values in CdoSnapshot"() {

        given:
        def json = new JsonBuilder()
        json {
            commitId "1.0"
            globalCdoId {
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
        CdoSnapshot snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        snapshot.getPropertyValue("_char") == 'a' as char
        snapshot.getPropertyValue("name") == "kaz"
        snapshot.getPropertyValue("age") == 1
        snapshot.getPropertyValue("flag") == true
    }

    def "should deserialize state with entity in CdoSnapshot"() {

        given:
        def json = new JsonBuilder()
        json {
            commitId "1.0"
            globalCdoId {
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
        CdoSnapshot snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        def entity = snapshot.getPropertyValue("dummyUserDetails")
        entity instanceof DummyUserDetails
    }

    def "should deserialize state with value object in CdoSnapshot"() {

        given:
        def json = new JsonBuilder()
        json {
            commitId "1.0"
            globalCdoId {
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
        CdoSnapshot snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        def valueObject = snapshot.getPropertyValue("dummyAddress")
        valueObject instanceof DummyAddress
    }

    def "should deserialize state with collections in CdoSnapshot"() {

        given:
        def json = new JsonBuilder()
        json {
            commitId "1.0"
            globalCdoId {
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
                name "kaz"
            }
        }

        when:
//        println json.toPrettyString()
        CdoSnapshot snapshot = javersTestAssembly().jsonConverter.fromJson(json.toString(), CdoSnapshot)

        then:
        snapshot.getPropertyValue("intArray") == [1, 2].toArray()
        snapshot.getPropertyValue("integerList") == [3, 4]
        snapshot.getPropertyValue("stringSet") == ["5", "6"] as Set
        snapshot.getPropertyValue("primitiveMap") == [time : new LocalDateTime(2000, 1, 1, 12, 0)]
    }
}