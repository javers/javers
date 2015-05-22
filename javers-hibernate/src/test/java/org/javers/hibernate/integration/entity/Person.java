package org.javers.hibernate.integration.entity;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

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

    public String getId() {
        return id;
    }

    public Person getBoss() {
        return boss;
    }

    public Person getBoss(int level) {
        if (level == 0){
            return this;
        }
        if (level == 1){
            return boss;
        }
        return boss.getBoss(level-1);
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
                '}';
    }
}
