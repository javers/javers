package org.javers.core.model;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pawel Cierpiatka
 */
public class Category extends AbstractCategory {

    @Id
    private Long id;

    public Category(Long id) {
        super("name "+id);
        this.id = id;
    }

    public Category(Long id, String name) {
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
