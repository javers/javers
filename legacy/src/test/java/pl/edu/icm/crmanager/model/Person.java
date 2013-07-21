package pl.edu.icm.crmanager.model;

import javax.jws.HandlerChain;
import javax.persistence.Embeddable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

@Embeddable
public class Person implements CrmComplexEmbeddable{
    public Person() {
        
    }
    
    public Person(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    private String firstName;
    private String lastName;
    private String email;
    
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
    	return "Person('"+firstName+"','"+lastName+"')";
    }
    
    @Override
    public int hashCode() {
    	return (""+firstName+lastName+email).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
    	return EqualsBuilder.reflectionEquals(this, obj);
    }
}
