package org.javers.spring.boot;

import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "employee")
public class EmployeeEntity {

  @Id
  @Column
  private UUID id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "department_id", referencedColumnName = "id")
  private DepartmentEntity department;

  @org.javers.core.metamodel.annotation.Id
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public DepartmentEntity getDepartment() {
    return department;
  }

  public void setDepartment(DepartmentEntity department) {
    this.department = department;
  }

  @Override
  public String toString() {
    return "EmployeeEntity{" + "id=" + id + '}';
  }
}
