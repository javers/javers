package org.javers.spring.boot;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
public class DummyEntity {

    @Id
    private int id;
    private String name;
    private ShallowEntity shallowEntity;

    DummyEntity() {
    }

    public DummyEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static DummyEntity random() {
        return new DummyEntity(UUID.randomUUID().hashCode(), UUID.randomUUID().toString());
    }

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    @ManyToOne
    public ShallowEntity getShallowEntity() {
        return shallowEntity;
    }

    public void setShallowEntity(ShallowEntity shallowEntity) {
        this.shallowEntity = shallowEntity;
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
