package org.javers.core.json;

import org.javers.common.collections.Lists;

import java.util.List;

/**
 * @author bartosz walacik
 */
public abstract class JsonTypeAdapterTemplate<T> implements JsonTypeAdapter<T>{
    public abstract Class getValueType();

    @Override
    public List<Class> getValueTypes() {
        return Lists.immutableListOf(getValueType());
    }
}
