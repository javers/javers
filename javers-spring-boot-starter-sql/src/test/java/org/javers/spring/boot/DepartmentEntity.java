package org.javers.spring.boot;

import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "department")
public class DepartmentEntity {

  @Id
  @Column
  @GeneratedValue
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
