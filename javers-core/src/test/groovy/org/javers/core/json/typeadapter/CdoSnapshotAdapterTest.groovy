package org.javers.core.json.typeadapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import groovy.json.JsonSlurper
import org.javers.core.JaversTestBuilder
import org.javers.core.commit.CommitId
import org.javers.core.json.JsonConverter
import org.javers.core.json.JsonConverterBuilder
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.DummyUser
import org.javers.core.snapshot.SnapshotFactory
import org.javers.test.builder.DummyUserBuilder
import spock.lang.Specification


class CdoSnapshotAdapterTest extends Specification {

    JsonConverter jsonConverter = JsonConverterBuilder.jsonConverter().build()

    def "should serialize CdoSnapshot to Json"() {

        given:
        JaversTestBuilder javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        SnapshotFactory snapshotFactory = javersTestBuilder.snapshotFactory

        DummyUser dummyUser = DummyUserBuilder.dummyUser("kazik").build()

        InstanceId instanceId = javersTestBuilder.instanceId(dummyUser)
        CdoSnapshot cdoSnapshot = snapshotFactory.create(dummyUser, instanceId)
        CommitId commitId = new CommitId(1, 0)
        cdoSnapshot.bindTo(commitId)

        when:
        def json = new JsonSlurper().parseText(jsonConverter.toJson(cdoSnapshot))

        then:
        json.commitId.majorId == 1
        json.commitId.minorId == 0
        json.state == [name: "kazik"]
    }

//    @Ignore
    def "should deserialize Json to CdoSnapshot"() {

        given:
        def json = /{"commitId":{"majorId":1,"minorId":0},"state":{"name":"kazik"}}/

        JsonElement jsonElement = new JsonParser().parse(json)

        CdoSnapshotAdapter cdoSnapshotAdapter = new CdoSnapshotAdapter()

        when:
        CdoSnapshot cdoSnapshot = cdoSnapshotAdapter.fromJson(jsonElement, Stub(JsonDeserializationContext))

        then:
        cdoSnapshot.commitId.majorId == 1
        cdoSnapshot.commitId.minorId == 0
        cdoSnapshot.state == [name: "kazik"]
    }

}
