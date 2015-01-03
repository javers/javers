package org.javers.core.metamodel.clazz;


import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
@javax.persistence.Entity
public class JpaEntity {
    @Id
    private int id;
}
