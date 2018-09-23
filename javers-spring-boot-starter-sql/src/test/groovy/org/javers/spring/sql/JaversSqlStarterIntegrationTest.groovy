package org.javers.spring.sql

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.DummyEntity
import org.javers.spring.boot.TestApplication
import org.javers.spring.boot.sql.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class JaversSqlStarterIntegrationTest extends Specification {

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build default javers instance with auto-audit aspect"() {
        when:
        def entity = DummyEntity.random()
        def persistedEntity = dummyEntityRepository.save(entity)

        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(persistedEntity.id, DummyEntity).build())

        then:
        assert snapshots.size() == 1
        assert snapshots[0].commitMetadata.properties["key"] == "ok"
        assert snapshots[0].commitMetadata.author == "unauthenticated"
    }
}