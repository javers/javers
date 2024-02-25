package org.javers.core.examples.vo;

import jakarta.persistence.Id;
import java.util.List;
import java.util.Map;

public class Person {

    private String login;
    private String name;

    Person() {
    }

    public Person(String login, String name) {
        this.login = login;
        this.name = name;
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


    @Override
    public String toString() {
        return "Person{" +
                "login='" + login + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}
    
    
}
