package org.javers.spring.boot.custom;

import java.util.UUID;
import org.javers.spring.boot.TestApplication;
import org.javers.spring.boot.custom.entity.DepartmentEntity;
import org.javers.spring.boot.custom.entity.EmployeeEntity;
import org.javers.spring.boot.sql.EmployeeRepository;
import org.javers.spring.boot.sql.EmployeeRepositoryWithJavers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class})
@ActiveProfiles("test")
@Commit
public class JaversIntegrationTest {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private EmployeeRepositoryWithJavers employeeRepositoryWithJavers;

  @Test
  public void testPersistingEntityWithoutJaversAuditingEnabled() {
    employeeRepository.save(getDummyEntity());
  }

  @Test
  public void testPersistingEntityWithJaversAuditingEnabled() {
    employeeRepositoryWithJavers.save(getDummyEntity());
  }

  private EmployeeEntity getDummyEntity() {
    DepartmentEntity departmentEntity = new DepartmentEntity();
    departmentEntity.setDepartmentName("DEPT1");

    EmployeeEntity employeeEntity = new EmployeeEntity();
    employeeEntity.setEmployeeName("ABC");
    employeeEntity.setId(UUID.randomUUID());
    employeeEntity.setDepartment(departmentEntity);

    return employeeEntity;
  }

}
