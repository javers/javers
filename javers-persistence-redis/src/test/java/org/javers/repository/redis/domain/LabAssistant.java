package org.javers.repository.redis.domain;

import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

@Entity
public class LabAssistant {

    @Id
    private String name;

    private String level;

    public LabAssistant(String name, String level) {
        super();
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return String.format("LabAssistant [name=%s, level=%s]", name, level);
    }
}
