package org.javers.repository.jql;

import org.javers.core.metamodel.object.GlobalId;
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

    public abstract GlobalId create(TypeMapper typeMapper);
}
