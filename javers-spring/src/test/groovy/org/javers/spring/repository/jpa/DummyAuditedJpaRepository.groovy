package org.javers.spring.repository.jpa

import org.javers.spring.annotation.JaversAuditable
import org.javers.spring.model.DummyObject
import org.springframework.stereotype.Repository

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

/**
 * @author bartosz walacik
 */
@Repository
class DummyAuditedJpaRepository {
    @PersistenceContext
    EntityManager entityManager

    @Transactional
    @JaversAuditable
    void save(DummyObject o){
        entityManager.persist(o)
    }
}
