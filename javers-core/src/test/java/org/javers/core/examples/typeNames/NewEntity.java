package org.javers.core.examples.typeNames;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

/**
 * @author bartosz.walacik
 */
@TypeName("org.javers.core.examples.typeNames.OldEntity")
public class NewEntity {
    @Id
    private int id;

    private int value;

    private int newValue;

    @Id
    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public int getNewValue() {
        return newValue;
    }
}
