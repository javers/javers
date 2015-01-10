package org.javers.spring.integration.domain;

import javax.persistence.Id;

/**
 * @author Pawel Szymczyk
 */
public class User {

    @Id
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
