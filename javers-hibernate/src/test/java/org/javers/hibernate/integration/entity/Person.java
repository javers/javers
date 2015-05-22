package org.javers.hibernate.integration.entity;

import javax.persistence.*;

@Entity
@Table(name = "person")
public class Person {

    @Id
    private String id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private Person boss;

    public Person() {
    }

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Person getBoss() {
        return boss;
    }

    public void setBoss(Person boss) {
        this.boss = boss;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", boss=" + boss +
                '}';
    }
}
