package org.javers.spring.integration.domain;

import javax.persistence.Id;

/**
 * @author Pawel Szymczyk
 */
public class Project {

    @Id
    private int id;
    private String name;

    public Project(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
