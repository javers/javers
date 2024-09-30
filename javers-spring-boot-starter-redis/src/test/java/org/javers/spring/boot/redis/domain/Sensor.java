package org.javers.spring.boot.redis.domain;

import java.io.Serializable;

import org.javers.core.metamodel.annotation.Value;

@Value
public class Sensor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Number value;
    private LabAssistant labAssistant;

    public Sensor(String name, Number value) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public LabAssistant getLabAssistant() {
        return labAssistant;
    }

    public void setLabAssistant(LabAssistant labAssistant) {
        this.labAssistant = labAssistant;
    }

    @Override
    public String toString() {
        return String.format("Sensor [name=%s, value=%s, labAssistant=%s]", name, value, labAssistant);
    }

}
