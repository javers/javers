package org.javers.common.reflection;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class ConcreteWithActualType extends AbstractGeneric<String, List<String>> {

    public ConcreteWithActualType(String s, List<String> value) {
        super(s, value);
    }
}
