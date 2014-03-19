package org.javers.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class AbstractCategory {

    private String name;
    private Category parent;
    private final List<Category> categories = new ArrayList<>();

    public AbstractCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getCategories() {
        return categories;
    }

}
