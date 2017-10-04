package org.javers.spring.auditable.aspect.springdata

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.sql.DummyEntity
import org.javers.spring.boot.sql.DummyEntityRepository
import org.javers.spring.boot.sql.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * @author pawel szymczyk
 */
@SpringBootTest(classes=[TestApplication])
class JaversSpringDataJpaAuditableRepositoryAspectTest extends Specification {

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository repository

    def "should create a new version on saveAndFlush via audited repository"() {
        setup:
        def o = new DummyEntity(1, "foo")

        when:
        repository.saveAndFlush(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyEntity).build())

        snapshots.size() == 1
        snapshots[0].initial
        snapshots[0].commitMetadata.properties["key"] == "ok"
        snapshots[0].commitMetadata.author == "unauthenticated"
    }
}