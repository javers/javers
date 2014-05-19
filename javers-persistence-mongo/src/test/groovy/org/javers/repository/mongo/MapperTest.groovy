package org.javers.repository.mongo

import com.mongodb.DBObject
import org.javers.core.commit.Commit
import org.javers.core.commit.CommitId
import org.javers.core.diff.Diff
import org.javers.core.json.JsonConverter
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.GlobalCdoId
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId

class MapperTest extends Specification{

    def "should map InstanceIdDTO to DBObject"() {

        given:
        def mapper = new Mapper()
        def dtoId = instanceId("kazik", DummyUser)

        when:
        def dtoIdAsDBObject = mapper.toDBObject(dtoId)

        then:
        dtoIdAsDBObject.get("globalCdoId").cdoId == "kazik"
        dtoIdAsDBObject.get("globalCdoId").entity == "org.javers.core.model.DummyUser"
    }

    def "should map Commit to DBObject"() {

        given:
        Mapper mapper = new Mapper()
        mapper.setJsonConverter(Stub(JsonConverter))

        Commit commit = new Commit(new CommitId(1, 0), "Kazik", [new CdoSnapshot(Stub(GlobalCdoId), [:])], Stub(Diff))

        when:
        DBObject commitAsDBObject = mapper.toDBObject(commit)

        then:
        println commitAsDBObject.toString()
        commitAsDBObject.containsField("globalCdoId")
        commitAsDBObject.containsField("snapshots")
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
}
