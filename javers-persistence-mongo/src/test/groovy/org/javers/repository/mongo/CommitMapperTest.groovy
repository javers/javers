package org.javers.repository.mongo

import com.mongodb.DBObject
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.commit.Commit
import spock.lang.Specification


class CommitMapperTest extends Specification{

    def "should correctly map Commit"() {

        given:
        CommitMapper commitMapper = new CommitMapper();
        DummyProduct dummyProduct = new DummyProduct(1, "Candy")

        Javers javers = JaversBuilder.javers()
                .registerEntity(DummyProduct)
                .build()

        Commit commit = javers.commit("charlie", dummyProduct)

        when:
        DBObject commitAsDBObject = commitMapper.toCdoSnapshot(commit)

        then:
        ["id", "snapshots", "author", "commitDate", "diff"].every {
            commitAsDBObject.containsField(it)
        }
    }


}
