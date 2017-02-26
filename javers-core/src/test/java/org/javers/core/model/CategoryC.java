package org.javers.core.model;

import javax.persistence.Id;

/**
 * @author Pawel Cierpiatka
 */
public class CategoryC extends AbstractCategory {

    @Id
    private Long id;

    public CategoryC(Long id) {
        super("name "+id);
        this.id = id;
    }

    public CategoryC(Long id, String name) {
        super(name);
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
