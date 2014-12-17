package org.javers.core.metamodel.object;

import org.javers.common.validation.Validate;
import org.javers.core.graph.LiveCdoFactory;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.clazz.ValueObject;
import org.javers.core.metamodel.type.TypeMapper;

/**
 * @author bartosz walacik
 */
public final class UnboundedValueObjectIdDTO extends GlobalIdDTO {
    private final Class  javaClass;

    UnboundedValueObjectIdDTO(Class javaClass) {
        Validate.argumentsAreNotNull(javaClass);
        this.javaClass = javaClass;
    }

    public static UnboundedValueObjectIdDTO unboundedMapId(){
        return new UnboundedValueObjectIdDTO(LiveGraphFactory.getMapWrapperType());
    }

    public static UnboundedValueObjectIdDTO unboundedSetId(){
        return new UnboundedValueObjectIdDTO(LiveGraphFactory.getSetWrapperType());
    }

    public static UnboundedValueObjectIdDTO unboundedListId(){
        return new UnboundedValueObjectIdDTO(LiveGraphFactory.getListWrapperType());
    }

    public static UnboundedValueObjectIdDTO unboundedValueObjectId(Class valueObjectClass) {
        return new UnboundedValueObjectIdDTO(valueObjectClass);
    }

    @Override
    public String value() {
        return javaClass.getName()+"/";
    }

    @Override
    UnboundedValueObjectId create(TypeMapper typeMapper) {
        return new UnboundedValueObjectId( typeMapper.getManagedClass(javaClass, ValueObject.class));
    }
}
