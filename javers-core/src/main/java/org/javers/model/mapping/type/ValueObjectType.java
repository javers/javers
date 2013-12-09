package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class ValueObjectType extends JaversType {
    protected ValueObjectType(Class baseJavaType) {
        super(baseJavaType);
    }
}