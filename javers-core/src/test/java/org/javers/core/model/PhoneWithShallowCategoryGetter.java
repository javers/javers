package org.javers.core.model;

import org.javers.core.metamodel.annotation.ShallowReference;

import javax.persistence.Id;

/**
 * @author michal wesolowski
 */
public class PhoneWithShallowCategoryGetter {
    @Id
    private Long id;
    private String number;
    private Category shallowCategory;
    private Category deepCategory;

    public PhoneWithShallowCategoryGetter(Long id, String number, Category shallowCategory, Category deepCategory) {
        this.id = id;
        this.number = number;
        this.shallowCategory = shallowCategory;
        this.deepCategory = deepCategory;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    @ShallowReference
    public Category getShallowCategory() {
        return shallowCategory;
    }

    public Category getDeepCategory() {
        return deepCategory;
    }
}
