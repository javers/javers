package org.javers.core.examples.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author bartosz walacik
 */
public class Employee {

    @Id
    private String name;

    private String position;

    private int salary;

    private Employee boss;

    private final List<Employee> subordinates = new ArrayList<>();

    private Address primaryAddress;
    private Address postalAddress;

    private int age;

    public Employee() {
    }

    public Employee(String name) {
        this(name, 10000);
    }

    public Employee(String name, int salary) {
        checkNotNull(name);
        this.name = name;
        this.salary = salary;
    }

    public Employee(String name, int salary, String position) {
        checkNotNull(name);
        checkNotNull(position);
        this.name = name;
        this.salary = salary;
        this.position = position;
    }

    public Employee addSubordinate(Employee employee) {
        checkNotNull(employee);
        employee.boss = this;
        subordinates.add(employee);
        return this;
    }

    public Employee addSubordinates(Employee... employees) {
        checkNotNull(employees);
        for (Employee e : employees){
            addSubordinate(e);
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public Employee getBoss() {
        return boss;
    }

    public int getSalary() {
        return salary;
    }

    public int getAge() {
        return age;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public List<Employee> getSubordinates() {
        return Collections.unmodifiableList(subordinates);
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Employee)) {
            return false;
        }
        Employee that = (Employee) obj;

        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("salary", salary)
                .append("boss", boss != null ? boss.name : "")
                .append("subordinates", subordinates.size())
                .toString();
    }
}
