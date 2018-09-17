package org.javers.spring.boot;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@JaversSpringDataAuditable
public interface EmployeeRepositoryWithJavers extends JpaRepository<EmployeeEntity, UUID> {
}
