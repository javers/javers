package org.javers.core.model;

import javax.persistence.Id;

/**
 * @author akrystian
 */
public class DummyUserContact {
    @Id
    private String name;

    private DummyUser person;

    private DummyAddress address;

    private DummyUserPhone propertyWithShallowReferenceAnn;

    public DummyUserContact(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DummyAddress getAddress() {
        return address;
    }

    public void setAddress(DummyAddress address) {
        this.address = address;
    }

    public DummyUserPhone getPropertyWithShallowReferenceAnn() {
        return propertyWithShallowReferenceAnn;
    }

    public void setPropertyWithShallowReferenceAnn(DummyUserPhone propertyWithShallowReferenceAnn) {
        this.propertyWithShallowReferenceAnn = propertyWithShallowReferenceAnn;
    }

    public DummyUser getPerson() {
        return person;
    }

    public void setPerson(DummyUser person) {
        this.person = person;
    }
}
