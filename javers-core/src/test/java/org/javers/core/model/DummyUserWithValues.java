package org.javers.core.model;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import java.math.BigDecimal;

/**
 * @author bartosz walacik
 */
public class DummyUserWithValues {
    @Id
    private final String name;

    private BigDecimal    salary;
    private LocalDateTime dob;
    private String        position;

    private DummyUserWithValues(String name) {
        this.name = name;
        this.dob = LocalDateTime.now();
    }

    private DummyUserWithValues(String name, LocalDateTime dob) {
        this.name = name;
        this.dob = dob;
    }

    private DummyUserWithValues(String name, BigDecimal salary) {
        this.name = name;
        this.salary = salary;
    }

    private DummyUserWithValues(String name, String position) {
        this.name = name;
        this.position = position;
    }

    public static DummyUserWithValues dummyUserWithDate(String name){
        return new DummyUserWithValues(name);
    }

    public static DummyUserWithValues dummyUserWithDate(String name, LocalDateTime dob){
        return new DummyUserWithValues(name,dob);
    }

    public static DummyUserWithValues dummyUserWithSalary(String name, BigDecimal salary){
        return new DummyUserWithValues(name,salary);
    }

    public static DummyUserWithValues dummyUserWithPosition(String name, String position){
        return new DummyUserWithValues(name,position);
    }

    public BigDecimal getSalary() {
        return salary;
    }

    @Id
    public String getName() {
        return name;
    }

    public LocalDateTime getDob() {
        return dob;
    }

    public String getPosition() {
        return position;
    }
}
