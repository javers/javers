package org.javers.hibernate.integration.entity;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@JaversSpringDataAuditable
public interface PersonCrudRepository extends JpaRepository<Person, String> {
}
