package org.javers.repository.mongo.model;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Value;

import javax.persistence.OneToMany;
import java.util.List;

/**
 * @author hank cp
 */
@Value
public class CrossReferenceObjectA {

    @OneToMany
    public List<CrossReferenceObjectB> bList;

    @OneToMany
    @DiffIgnore
    public List<CrossReferenceObjectB> bListIgnored;

}
