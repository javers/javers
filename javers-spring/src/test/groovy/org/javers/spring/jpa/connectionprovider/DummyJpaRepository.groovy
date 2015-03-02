package org.javers.spring.jpa.connectionprovider

import org.javers.spring.annotation.JaversAuditable
import org.javers.spring.model.DummyObject

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

/**
 * @author bartosz walacik
 */
class DummyJpaRepository {
    @PersistenceContext
    EntityManager entityManager

    @Transactional
    @JaversAuditable
    void save(DummyObject o){
        entityManager.persist(o)
    }
}
