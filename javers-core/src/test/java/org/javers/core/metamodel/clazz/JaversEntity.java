package org.javers.core.metamodel.clazz;

import org.javers.core.metamodel.annotation.Id;

/**
 * @author bartosz walacik
 */
@org.javers.core.metamodel.annotation.Entity
public class JaversEntity {
    @Id
    private int id;
}
