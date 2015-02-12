package org.javers.repository.sql.domain

import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import org.javers.repository.sql.reposiotries.CommitMetadataRepository
import org.joda.time.LocalDateTime

/**
 * @author pawel szymczyk
 */
class CommitMetadataRepositoryTest extends BaseRepositoryTest {

    def "should save commit metadata"() {

        given:
        def commitMetadata = new CommitMetadata("author", LocalDateTime.now(), new CommitId(1L, 0))
        def commitMetadataRepository = sqlRepoBuilder.getComponent(CommitMetadataRepository)

        when:
        def primaryKey = commitMetadataRepository.save(commitMetadata.author, commitMetadata.commitDate, commitMetadata.id)
        dbConnection.commit()

        then:
        primaryKey != null
    }
}
