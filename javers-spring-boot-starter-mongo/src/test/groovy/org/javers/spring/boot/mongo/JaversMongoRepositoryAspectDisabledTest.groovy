package org.javers.spring.boot.mongo

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

/**
 * @author mwesolowski
 */
@ActiveProfiles("test")
class JaversMongoRepositoryAspectDisabledTest extends BaseSpecification {

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    @Autowired
    JaversMongoProperties javersProperties

    def "should build javers instance without auto-audit aspect when disabled"() {
        when:
        def dummyEntity = dummyEntityRepository.save(new DummyEntity(UUID.randomUUID().hashCode()))

        then:
        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(dummyEntity.id, DummyEntity).build())
        assert snapshots.size() == 0
    }
}
