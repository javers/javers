package org.javers.model.mapping.type;

import org.javers.common.validation.Validate;

import java.lang.reflect.Type;
import java.util.Collection;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {

    public CollectionType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    protected Class initElementType() {
        if (getActualClassTypeArguments().size() == 1) {
            return  getActualClassTypeArguments().get(0);
        }

        return null;
    }
}
