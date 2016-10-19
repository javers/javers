package org.javers.core.model

import org.javers.core.metamodel.annotation.ShallowReference
import javax.persistence.Id

/**
 * @author michal wesolowski
 */
class PhoneWithShallowCategory {
    @Id
    private Long id
    private String number = "123"
    @ShallowReference
    private Category shallowCategory
    private Category deepCategory

    PhoneWithShallowCategory(Long id, Category shallowCategory, Category deepCategory) {
        this.id = id
        this.shallowCategory = shallowCategory
        this.deepCategory = deepCategory
    }

    PhoneWithShallowCategory(Long id, Category shallowCategory) {
        this(id, shallowCategory, null)
    }

    Long getId() {
        id
    }

    @ShallowReference
    Category getShallowCategory() {
        shallowCategory
    }

    Category getDeepCategory() {
        deepCategory
    }
}
