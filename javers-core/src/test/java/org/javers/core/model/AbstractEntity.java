package org.javers.core.model;

import java.io.Serializable;

/**
 * Created by Ian Agius
 */
public abstract class AbstractEntity<ID extends Serializable> {
    public abstract ID getId();
}
