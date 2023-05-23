package org.javers.core.examples.model;

import jakarta.persistence.Id;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Geometry;

public class Person {
    @Id
    private String login;
    private String name;
    private List<Address> addresses;
    private Map<String, Address> addressMap;
    private Position position;
    private Geometry coordinates;

    Person() {
    }

    public Person(String login, String name) {
        this.login = login;
        this.name = name;
    }

    public Person(String login, String name, Geometry coordinates) {
        this.login = login;
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Person{" +
                "login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}
