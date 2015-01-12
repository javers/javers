package org.javers.common.reflection;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class ConcreteWithActualType extends AbstractGeneric<List<String>> {

    public ConcreteWithActualType(List<String> value) {
        super(value);
    }
}
