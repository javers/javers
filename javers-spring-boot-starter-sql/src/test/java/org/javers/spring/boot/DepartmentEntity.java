package org.javers.spring.boot;
import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "department")
public class DepartmentEntity {
  @Id
  @Column
  private UUID id;

  @OneToMany(mappedBy = "department")
  private List<EmployeeEntity> employee;

  @org.javers.core.metamodel.annotation.Id
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public List<EmployeeEntity> getEmployee() {
    return employee;
  }

  public void setEmployee(List<EmployeeEntity> employee) {
    this.employee = employee;
  }

  @Override
  public String toString() {
    return "DepartmentEntity{" + "id=" + id + '}';
  }
}