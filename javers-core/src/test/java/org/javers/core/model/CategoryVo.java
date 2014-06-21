package org.javers.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class CategoryVo {
    private String name;
    private CategoryVo parent;
    private List<CategoryVo> children = new ArrayList<>();

    public CategoryVo(String name) {
        this.name = name;
    }

    public CategoryVo addChild(CategoryVo child) {
        child.setParent(this);
        children.add(child);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryVo getParent() {
        return parent;
    }

    public void setParent(CategoryVo parent) {
        this.parent = parent;
    }

    public List<CategoryVo> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryVo> children) {
        this.children = children;
    }
}
