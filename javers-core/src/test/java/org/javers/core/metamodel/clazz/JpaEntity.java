package org.javers.core.metamodel.clazz;


import jakarta.persistence.Id;

/**
 * @author bartosz walacik
 */
@jakarta.persistence.Entity
public class JpaEntity {
    @Id
    private int id;
}
