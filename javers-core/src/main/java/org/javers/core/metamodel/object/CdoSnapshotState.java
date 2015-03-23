package org.javers.core.metamodel.object;

import org.javers.common.collections.Defaults;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.MapEnumeratorContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotState {
    private final Map<Property, Object> properties;

    CdoSnapshotState(Map<Property, Object> state) {
        Validate.argumentIsNotNull(state);
        this.properties = state;
    }

    int size(){
        return properties.size();
    }

    public Object getPropertyValue(Property property) {
        Validate.argumentIsNotNull(property);
        Object val = properties.get(property);
        if (val == null){
            return Defaults.defaultValue(property.getType());
        }
        return val;
    }

    boolean isNull(Property property) {
        Validate.argumentIsNotNull(property);
        return !properties.containsKey(property);
    }

    public Set<Property> getProperties() {
        return Collections.unmodifiableSet(properties.keySet());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CdoSnapshotState)) return false;

        CdoSnapshotState that = (CdoSnapshotState) o;

        return properties.equals(that.properties);
    }

    /**
     * List of properties with changed values (when comparing to previous state)
     */
    public List<Property> differentValues(CdoSnapshotState previous){
        for (Map.Entry<Property, Object> entries : properties.entrySet()){
            //Object
        }
        return null;
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }
}
