package org.javers.core.metamodel.clazz;

import jakarta.persistence.Id;

/**
 * @author bartosz walacik
 */
public class ClassWithIdAnn {
    @Id
    private String some;
}
