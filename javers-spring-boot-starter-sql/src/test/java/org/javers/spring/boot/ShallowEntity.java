package org.javers.spring.boot;

import org.javers.core.metamodel.annotation.ShallowReference;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@ShallowReference
public class ShallowEntity {
    @Id
    private int id;
    private String value;

    ShallowEntity() {
    }

    public ShallowEntity(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static ShallowEntity random() {
        return new ShallowEntity(UUID.randomUUID().hashCode(), UUID.randomUUID().toString());
    }

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
