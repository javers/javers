package org.javers.core.examples.typeNames;

/**
 * @author bartosz.walacik
 */
public class OldValueObject extends AbstractValueObject{
    private int oldField;

    public OldValueObject(int someValue, int oldField) {
        super(someValue);
        this.oldField = oldField;
    }
}
