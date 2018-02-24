package org.javers.core.examples.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.javers.common.collections.Sets;
import org.javers.core.metamodel.annotation.TypeName;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author bartosz walacik
 */
@TypeName("Employee")
public class Employee {

    @Id
    private String name;

    private Position position;

    private int salary;

    private int age;

    private Employee boss;

    private List<Employee> subordinates = new ArrayList<>();

    private Address primaryAddress;

    private Address postalAddress;

    private Set<String> skills;

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
        this.position = Position.valueOf(position);
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

    public Position getPosition() {
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

    public Set<String> getSkills() {
        return Collections.unmodifiableSet(this.skills);
    }

    public void setPosition(String position) {
        this.position = Position.valueOf(position);
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSkills(String... skills) {
        this.skills = Sets.asSet(skills);
    }

    public void setBoss(Employee boss) {
        this.boss = boss;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
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
