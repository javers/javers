package org.javers.core.metamodel.object;

import org.javers.core.metamodel.type.TypeMapper;

/**
* @author bartosz walacik
*/
public abstract class GlobalIdDTO {
    public abstract String value();

    @Override
    public String toString() {
        return "Dto("+value()+")";
    }

    abstract GlobalId create(TypeMapper typeMapper);
}
