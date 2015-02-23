package org.javers.repository.sql.reposiotries

import org.javers.common.collections.Optional
import org.javers.core.metamodel.object.InstanceId
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class GlobalIdRepositoryTest extends Specification {
    def "should not query if given globalId is already persisted"() {
        given:
        def persistentInstanceId = new PersistentGlobalId(Stub(InstanceId), Optional.of(5L))
        def globalIdRepository = new GlobalIdRepository()

        when:
        def found = globalIdRepository.findPersistedGlobalId(persistentInstanceId)

        then:
        found.primaryKey == 5
    }
}
