package org.javers.repository.sql.domain

import org.javers.core.JaversBuilder
import org.javers.core.commit.CommitId
import org.javers.core.model.DummyUser
import org.joda.time.LocalDateTime

/**
 * @author pawel szymczyk 
 */
class CdoSnapshotRepositoryTest extends BaseRepositoryTest {

    def "should save and next find persisted CdoSnapshot"() {

        given:
        def javers = JaversBuilder.javers().build();
        def commitMetadataRepository = sqlRepoBuilder.getComponent(CommitMetadataRepository)
        def globalIdRepository = sqlRepoBuilder.getComponent(GlobalIdRepository)
        globalIdRepository.setJsonConverter(javers.jsonConverter)
        def cdoSnapshotRepository = sqlRepoBuilder.getComponent(CdoSnapshotRepository)
        cdoSnapshotRepository.setJsonConverter(javers.jsonConverter)

        def dummyUser = new DummyUser("kazik", "kazikowski")
        def commit = javers.commit("author", dummyUser)
        def snapshot = commit.getSnapshots().get(0)
        
        def globalIdPk = globalIdRepository.save(commit.getSnapshots().get(0).globalId)
        def commitPk = commitMetadataRepository.save("author", LocalDateTime.now(), new CommitId(1,01))

        when:
        def primaryKey = cdoSnapshotRepository.save(globalIdPk, commitPk, snapshot)
        dbConnection.commit()

        then:
        primaryKey != null
    }
}
