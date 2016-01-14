package org.javers.core.examples.typeNames;

import org.javers.core.metamodel.annotation.TypeName;

/**
 * @author bartosz.walacik
 */
@TypeName("org.javers.core.examples.typeNames.OldValueObject")
public class NewNamedValueObject extends AbstractValueObject {
    private int newField;

    public NewNamedValueObject(int someValue, int newField) {
        super(someValue);
        this.newField = newField;
    }
}
