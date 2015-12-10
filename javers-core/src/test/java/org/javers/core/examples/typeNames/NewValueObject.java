package org.javers.core.examples.typeNames;

/**
 * @author bartosz.walacik
 */
public class NewValueObject extends AbstractValueObject {
    private int newField;

    public NewValueObject(int someValue, int newField) {
        super(someValue);
        this.newField = newField;
    }
}
