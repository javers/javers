package org.javers.core.metamodel.type;

import org.javers.common.collections.Lists;

import java.lang.reflect.Type;
import java.util.List;

public abstract class SpecifiedClassCollectionType extends CollectionType {

    private final Class itemClass;

    public SpecifiedClassCollectionType(Type baseJavaType, Class itemClass) {
        super(baseJavaType);
        this.itemClass = itemClass;
    }

    @Override
    public boolean isFullyParametrized() {
        return true;
    }

    @Override
    public Type getItemType() {
        return itemClass;
    }

    @Override
    public Class getItemClass() {
        return itemClass;
    }

    @Override
    public List<Type> getActualTypeArguments() {
        return Lists.asList((Type)itemClass);
    }

}
