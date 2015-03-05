package org.javers.spring.integration

import org.javers.spring.annotation.JaversAuditable

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

/**
 * @author bartosz walacik
 */
class DummyAuditedJpaRepository {
    @PersistenceContext
    EntityManager entityManager

    @Transactional
    @JaversAuditable
    void save(DummyObject o){
        entityManager.persist(o)
    }
}
