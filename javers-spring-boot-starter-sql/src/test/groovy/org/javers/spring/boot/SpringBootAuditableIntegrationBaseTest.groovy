package org.javers.spring.boot

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.sql.DummyEntity
import org.javers.spring.boot.sql.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

abstract class SpringBootAuditableIntegrationBaseTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "@JaversSpringDataAuditable aspect should work with spring-boot"(){
        when:
        def entity = DummyEntity.random()
        dummyEntityRepository.save(entity)

        then:
        javers.findSnapshots( QueryBuilder.byInstanceId(entity.id, DummyEntity).build() ).size() == 1
    }
}
