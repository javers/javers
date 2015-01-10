package org.javers.spring.integration;

import javax.persistence.Id;

/**
 * @author Pawel Szymczyk
 */
class User {

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
