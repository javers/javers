package org.javers.repository.sql.domain

import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import org.joda.time.LocalDateTime

/**
 * @author pawel szymczyk
 */
class CommitRepositoryTest extends BaseRepositoryTest {

    def "should save and next find persisted commit metadata"() {

        given:
        def commitMetadata = new CommitMetadata("author", LocalDateTime.now(), new CommitId(1L, 0))
        def commitRepository = sqlRepoBuilder.getComponent(CommitRepository)

        when:
        def primaryKey = commitRepository.save(commitMetadata)
        dbConnection.commit()

        then:
        primaryKey != null

        when:
        def persistedPrimaryKey = commitRepository.save(commitMetadata)

        then:
        persistedPrimaryKey == primaryKey
    }
}
