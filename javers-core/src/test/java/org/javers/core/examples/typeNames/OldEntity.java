package org.javers.core.examples.typeNames;

import org.javers.core.metamodel.annotation.Id;

/**
 * @author bartosz.walacik
 */
public class OldEntity {
    @Id
    private int id;

    private int value;

    private int oldValue;

    @Id
    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public int getOldValue() {
        return oldValue;
    }
}
