package org.javers.model.mapping.type;

/**
 * @author bartosz walacik
 */
public abstract class ContainerType extends JaversType {

    protected ContainerType(Class baseJavaType) {
        super(baseJavaType);
    }
}
