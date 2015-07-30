package org.javers.spring.boot

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
abstract class SpringBootAuditableIntegrationBaseTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository


    def "@JaversSpringDataAuditable aspect should work with spring-boot"(){
        when:
        def entity = new DummyEntity(id:1)
        dummyEntityRepository.save(entity)

        then:
        javers.findSnapshots( QueryBuilder.byInstanceId("1", DummyEntity).build() ).size() == 1
    }
}
