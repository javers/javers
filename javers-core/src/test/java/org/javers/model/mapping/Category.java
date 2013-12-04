package org.javers.model.mapping;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pawel Cierpiatka
 */
public class Category {


    private Long id;
    private String name;
    private Category parent;
    private List<Category> categorys = new ArrayList<>();

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<Category> categorys) {
        this.categorys = categorys;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public void addChild(Category child) {
        child.setParent(this);
        categorys.add(child);
    }
}
