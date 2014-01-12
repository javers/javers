package org.javers.core.model;

import org.joda.time.LocalDateTime;

import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @author bartosz walacik
 */
public class DummyUserWithValues {
    @Id
    private String name;

    private BigDecimal    salary;
    private LocalDateTime dob;

    public DummyUserWithValues(String name) {
        this.name = name;
        this.dob = new LocalDateTime();
    }

    public DummyUserWithValues(String name, LocalDateTime dob) {
        this.name = name;
        this.dob = dob;
    }

    public DummyUserWithValues(String name, BigDecimal salary) {
        this.name = name;
        this.salary = salary;
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

    public BigDecimal getSalary() {
        return salary;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDob() {
        return dob;
    }
}
