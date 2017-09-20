package org.javers.spring.boot.mongo

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import spock.lang.Specification

/**
 * @author mwesolowski
 */
@SpringBootTest(classes = [TestApplication], properties = ["javers.springDataAuditableRepositoryAspectEnabled=false"])
@ActiveProfiles("test")
class JaversMongoRepositoryAspectDisabledTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build javers instance without auto-audit aspect"() {
        when:
        def dummyEntity = dummyEntityRepository.save(new DummyEntity(UUID.randomUUID().hashCode()))

        then:
        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(dummyEntity.id, DummyEntity).build())
        assert snapshots.size() == 0
    }
}
