package org.javers.spring.boot.sql;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author pawelszymczyk
 */
@Entity
public class DummyEntity {

    private int id;
    private String name;

    public DummyEntity() {
    }

    public DummyEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
