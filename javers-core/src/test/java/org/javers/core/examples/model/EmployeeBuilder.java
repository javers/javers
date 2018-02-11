package org.javers.core.examples.model;

import org.javers.common.collections.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class EmployeeBuilder {

    private String name;

    private String position;

    private int salary;

    private int age;

    private Employee boss;

    private Employee[] subordinates;

    private Address primaryAddress;

    private Address postalAddress;

    private String[] skills;

    private EmployeeBuilder() {
    }

    public static EmployeeBuilder Employee(String name) {
        return new EmployeeBuilder().withName(name);
    }

    public EmployeeBuilder withPosition(String position) {
        this.position = position;
        return this;
    }

    public EmployeeBuilder withSalary(int salary) {
        this.salary = salary;
        return this;
    }

    public EmployeeBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public EmployeeBuilder withBoss(Employee boss) {
        this.boss = boss;
        return this;
    }

    public EmployeeBuilder withPrimaryAddress(Address address) {
        this.primaryAddress = address;
        return this;
    }

    public EmployeeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public EmployeeBuilder withSubordinates(Employee... subordinates) {
        this.subordinates = subordinates;
        return this;
    }

    public EmployeeBuilder withSkills(String... skills) {
        this.skills = skills;
        return this;
    }

    public Employee build() {
        Employee employee = new Employee(name);

        if (position != null) {
            employee.setPosition(position);
        }

        employee.setSalary(salary);
        employee.setAge(age);
        employee.setBoss(boss);

        if (subordinates != null) {
            employee.addSubordinates(subordinates);
        }

        employee.setPrimaryAddress(primaryAddress);

        if (skills != null) {
            employee.setSkills(skills);
        }

        return employee;
    }
}
