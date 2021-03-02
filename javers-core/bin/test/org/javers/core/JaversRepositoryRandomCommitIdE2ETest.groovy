package org.javers.core

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.commit.CommitId
import org.javers.repository.jql.QueryBuilder

class JaversRepositoryRandomCommitIdE2ETest extends JaversRepositoryShadowE2ETest {

    def "should prevent from using toCommitId() filter with RANDOM CommitIdGenerator" () {
        when:
        def query = QueryBuilder
                .anyDomainObject()
                .toCommitId(CommitId.valueOf("4900110407498891977.00"))
                .build()
        javers.findSnapshots(query)

        then:
        def e = thrown(JaversException)
        e.code == JaversExceptionCode.MALFORMED_JQL
        println e
    }

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }
}
