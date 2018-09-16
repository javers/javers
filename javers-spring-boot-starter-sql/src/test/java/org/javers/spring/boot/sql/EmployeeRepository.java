package org.javers.spring.boot.sql;

import java.util.UUID;
import org.javers.spring.boot.custom.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

}
