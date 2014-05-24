package org.javers.repository.mongo

import groovy.json.JsonOutput
import org.javers.common.date.FakeDateProvider
import org.javers.core.JaversTestBuilder
import org.javers.core.commit.CommitId
import org.javers.core.json.JsonConverter
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId

class MapperTest extends Specification{


    def "should map Commit to DBObject"() {

        given:
        def dateProvider = new FakeDateProvider(year: 2000, monthOfYear: 1, dayOfMonth: 1, hourOfDay: 12, minuteOfHour: 0)
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly(dateProvider)
        def mapper = new Mapper(javersTestBuilder.jsonConverter)
        def kazik = new DummyUser("kazik")
        def commit = javersTestBuilder.commitFactory.create("andy", kazik)

        when:
        def dBObject = mapper.toDBObject(commit)

        then:
        println JsonOutput.prettyPrint(dBObject.toString())

        dBObject.get("commitId") == "1.0"

        with (dBObject.get("globalCdoId")) {
            it.get("cdoId") == "kazik"
            it.get("entity") == "org.javers.core.model.DummyUser"
        }

        dBObject.get("author") == "andy"

        //TODO
        dBObject.get("date")

        dBObject.get("snapshots").size() == 1
        dBObject.get("diff").size() == 2
    }
    def "should map CommitId to DBObject"() {

        given:
        def mapper = new Mapper()
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        mapper.setJsonConverter(javersTestBuilder.jsonConverter)

        def commitId = new CommitId(1, 0)

        when:
        def commitIdAsDBObject = mapper.toDBObject(commitId)

        then:
        commitIdAsDBObject.toString() == "1.0"
    }


    def "should map DBObject to CdoSnapshot"() {

        given:
        Mapper mapper = new Mapper();

        JsonConverter jsonConverter = Stub()
        mapper.setJsonConverter(jsonConverter)


        when:
        List<CdoSnapshot> cdoSnapshot = mapper.toCdoSnapshots()

        then:
        cdoSnapshot
    }

    def "should map InstanceIdDTO to DBObject"() {

        given:
        def mapper = new Mapper()
        def dtoId = instanceId("kazik", DummyUser)

        when:
        def dtoIdAsDBObject = mapper.toDBObject(dtoId)

        then:
        dtoIdAsDBObject.get("cdoId") == "kazik"
        dtoIdAsDBObject.get("entity") == "org.javers.core.model.DummyUser"
    }

    def "should map InstanceId to DBObject"() {

        given:
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def mapper = new Mapper(javersTestBuilder.jsonConverter)
        def instanceId = javersTestBuilder.globalIdFactory.createFromId(1, DummyUser)

        when:
        def dbObject = mapper.toDBObject(instanceId)

        then:
        dbObject.get("entity") == "org.javers.core.model.DummyUser"
        dbObject.get("cdoId") == 1
    }
}
