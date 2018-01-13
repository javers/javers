package org.javers.spring.boot

import org.javers.core.Javers
import org.javers.spring.boot.sql.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static org.javers.repository.jql.QueryBuilder.byInstanceId

abstract class SpringBootAuditableIntegrationBaseTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "@JaversSpringDataAuditable aspect should work effortlessly with spring-boot"(){
        when:
        def o = DummyEntity.random()

        dummyEntityRepository.save(o)
        o.name = "a"
        dummyEntityRepository.saveAndFlush(o)

        then:
        javers.findSnapshots( byInstanceId(o.id, DummyEntity).build() ).size() == 2
    }
}
