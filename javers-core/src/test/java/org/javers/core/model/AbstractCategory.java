package org.javers.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class AbstractCategory {

    private String name;
    private AbstractCategory parent;
    private final List<AbstractCategory> categories = new ArrayList<>();

    public AbstractCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AbstractCategory getParent() {
        return parent;
    }

    public void setParent(CategoryC parent) {
        this.parent = parent;
    }

    public List<AbstractCategory> getCategories() {
        return categories;
    }

    public void addChild(AbstractCategory child) {
        child.parent = this;
        getCategories().add(child);
    }

}
