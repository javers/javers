package org.javers.core.model

import org.javers.core.metamodel.annotation.ShallowReference
import javax.persistence.Id

/**
 * @author michal wesolowski
 */
class PhoneWithShallowCategory {
    @Id
    Long id
    String number = "123"
    @ShallowReference
    Category shallowCategory
    Category deepCategory

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
