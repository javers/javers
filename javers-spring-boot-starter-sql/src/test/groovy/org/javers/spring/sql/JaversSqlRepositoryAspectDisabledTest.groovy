package org.javers.spring.sql

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.DummyEntity
import org.javers.spring.boot.TestApplication
import org.javers.spring.boot.sql.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * @author mwesolowski
 */
@SpringBootTest(classes = [TestApplication], properties = ["javers.springDataAuditableRepositoryAspectEnabled=false"])
class JaversSqlRepositoryAspectDisabledTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build javers instance without auto-audit aspect"() {
        when:
        def entity = DummyEntity.random()
        dummyEntityRepository.save(entity)

        then:
        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(entity.id, DummyEntity).build())
        assert snapshots.size() == 0
    }
}