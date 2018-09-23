package org.javers.spring.boot;

import java.util.UUID;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

@JaversSpringDataAuditable
public interface EmployeeRepositoryWithJavers extends JpaRepository<EmployeeEntity, UUID> {
}
