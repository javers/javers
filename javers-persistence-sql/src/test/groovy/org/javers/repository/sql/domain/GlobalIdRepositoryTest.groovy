package org.javers.repository.sql.domain

import org.javers.core.JaversBuilder
import org.javers.core.model.DummyUser

class GlobalIdRepositoryTest extends BaseRepositoryTest {

    def "should select or insert"() {
        given:
        def javers = JaversBuilder.javers().build();
        def instanceId = javers.idBuilder().instanceId(new DummyUser("kazik"))
        def globalIdRepository = sqlRepoBuilder.getComponent(GlobalIdRepository)

        when:
        def id = globalIdRepository.save(instanceId)

        then:
        id != null

        when:
        def nextId = globalIdRepository.save(instanceId)

        then:
        nextId == id
    }
}