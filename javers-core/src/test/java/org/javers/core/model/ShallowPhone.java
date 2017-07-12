package org.javers.core.model;

import org.javers.core.metamodel.annotation.ShallowReference;

import javax.persistence.Id;

/**
 * @author akrystian
 */
@ShallowReference
public class ShallowPhone {
    @Id
    private Long id;
    private String number;
    private CategoryC category;

    public ShallowPhone(Long id, String number, CategoryC category) {
        this.id = id;
        this.number = number;
        this.category = category;
    }

    public ShallowPhone(Long id) {
        this.id = id;
    }

    @Id
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

    public CategoryC getCategory() {
        return category;
    }

    public void setCategory(CategoryC category) {
        this.category = category;
    }
}
