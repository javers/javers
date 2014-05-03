package org.javers.repository.mongo

import com.mongodb.DBObject
import org.javers.core.commit.Commit
import org.javers.core.commit.CommitId
import org.javers.core.diff.Diff
import org.javers.core.json.JsonConverter
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.GlobalCdoId
import spock.lang.Specification

class CommitMapperTest extends Specification{

    def "should map Commit to DBObject"() {

        given:
        CommitMapper commitMapper = new CommitMapper()
        commitMapper.setJsonConverter(Stub(JsonConverter))

        Commit commit = new Commit(new CommitId(1, 0), "Kazik", [new CdoSnapshot(Stub(GlobalCdoId), [:])], Stub(Diff))

        when:
        DBObject commitAsDBObject = commitMapper.toDBObject(commit)

        then:
        println commitAsDBObject.toString()
        commitAsDBObject.keySet().contains("globalCdoId")
        commitAsDBObject.keySet().contains("snapshots")
    }

    def "should map DBObject to CdoSnapshot"() {

        given:
        CommitMapper commitMapper = new CommitMapper();

        JsonConverter jsonConverter = Stub()
        commitMapper.setJsonConverter(jsonConverter)


        when:
        List<CdoSnapshot> cdoSnapshot = commitMapper.toCdoSnapshots()

        then:
        cdoSnapshot
    }
}
