package org.javers.spring.jpa

import org.javers.hibernate.integration.entity.Person
import org.javers.hibernate.integration.entity.PersonCrudRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Service
class ErrorThrowingService {
    private PersonCrudRepository repository

    @Autowired
    ErrorThrowingService(PersonCrudRepository repository) {
        this.repository = repository
    }

    @Transactional
    void saveAndThrow(Person person) {
        repository.save(person)
        throw new RuntimeException("rollback")
    }
}
