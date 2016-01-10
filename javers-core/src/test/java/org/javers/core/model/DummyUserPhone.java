package org.javers.core.model;

import org.javers.core.metamodel.annotation.ShallowReference;

import javax.persistence.Id;

/**
 * @author akrystian
 */
@ShallowReference
public class DummyUserPhone {
    @Id
    private Long id;

    private Category category;
    private String number;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
