package org.javers.spring.jpa

import org.javers.core.Javers
import org.javers.core.metamodel.object.SnapshotType
import org.javers.repository.jql.QueryBuilder
import org.javers.shadow.Shadow
import org.javers.spring.example.JaversSpringJpaApplicationConfig
import org.javers.spring.model.DummyObjectAuditable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.transaction.Transactional

import static org.javers.core.metamodel.object.SnapshotType.TERMINAL
import static org.javers.core.metamodel.object.SnapshotType.UPDATE

@Transactional
@EnableJaversEntityListeners
@ContextConfiguration(classes = [JaversSpringJpaApplicationConfig, JaversEntityListenerTest])
class JaversEntityListenerTest extends Specification {

    @Autowired
    Javers javers
    @Autowired
    EntityManager entityManager

    def "should commit audit on persist"() {
        entityManager.persist(new DummyObjectAuditable("name"))
        entityManager.flush()

        expect:
        javers.findShadows(QueryBuilder.byClass(DummyObjectAuditable).build()).size() == 1
    }

    def "should commit audit on update"() {
        def object = new DummyObjectAuditable("name")
        entityManager.persist(object)
        entityManager.flush()

        object.name = "new name"
        entityManager.persist(object)
        entityManager.flush()

        expect:
        javers.findShadows(QueryBuilder.byClass(DummyObjectAuditable).withSnapshotType(UPDATE).build())
                .size() == 1
    }

    def "should commit shallow delete on delete"() {
        def object = new DummyObjectAuditable("name")
        entityManager.persist(object)
        entityManager.flush()

        entityManager.remove(object)
        entityManager.flush()

        expect:
        javers.findShadows(QueryBuilder.byClass(DummyObjectAuditable).withSnapshotType(TERMINAL).build())
                .size() == 1
    }
}
