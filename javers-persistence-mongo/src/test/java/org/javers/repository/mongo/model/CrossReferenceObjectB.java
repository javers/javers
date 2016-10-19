package org.javers.repository.mongo.model;

import org.javers.core.metamodel.annotation.Value;

/**
 * @author hank cp
 */
@Value
public class CrossReferenceObjectB {

    public int value;

    public CrossReferenceObjectA a;

    public CrossReferenceObjectB() {}

    public CrossReferenceObjectB(int value, CrossReferenceObjectA a) {
        this.value = value;
        this.a = a;
    }
}
