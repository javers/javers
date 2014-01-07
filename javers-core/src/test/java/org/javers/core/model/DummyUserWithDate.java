package org.javers.core.model;

import org.joda.time.LocalDateTime;

import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
public class DummyUserWithDate {
    @Id
    private String name;

    private LocalDateTime dob;

    public DummyUserWithDate(String name) {
        this.name = name;
        this.dob = new LocalDateTime();
    }

    public DummyUserWithDate(String name, LocalDateTime dob) {
        this.name = name;
        this.dob = dob;
    }

    public static DummyUserWithDate dummyUserWithDate(String name){
        return new DummyUserWithDate(name);
    }

    public static DummyUserWithDate dummyUserWithDate(String name, LocalDateTime dob){
        return new DummyUserWithDate(name,dob);
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDob() {
        return dob;
    }
}
