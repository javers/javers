package org.javers.spring.jpa

import org.javers.hibernate.entity.Person
import org.javers.hibernate.entity.PersonCrudRepository

import javax.transaction.Transactional

class ErrorThrowingService {
    private PersonCrudRepository repository

    ErrorThrowingService(PersonCrudRepository repository) {
        this.repository = repository
    }

    @Transactional
    void saveAndThrow(Person person) {
        repository.save(person)
        throw new RuntimeException("rollback")
    }
}
