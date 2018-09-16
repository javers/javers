package org.javers.spring.boot.sql;

import java.util.UUID;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.boot.custom.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@JaversSpringDataAuditable
public interface EmployeeRepositoryWithJavers extends JpaRepository<EmployeeEntity, UUID> {

}
