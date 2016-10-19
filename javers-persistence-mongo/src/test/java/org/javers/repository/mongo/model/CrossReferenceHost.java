package org.javers.repository.mongo.model;

import javax.persistence.Id;

/**
 * @author hank cp
 */
public class CrossReferenceHost {

    @Id
    public long id;

    public CrossReferenceObjectA a;

}
