package org.javers.core.examples;

import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
public class Person {
    @Id
    private String login;
    private String firstName;
    private String lastName;

    public Person(String login, String firstName, String lastName) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
