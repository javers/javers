package org.javers.core.metamodel.object;

import org.javers.common.collections.Defaults;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.util.*;

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

        if (this.properties.size() != that.properties.size()){
            return false;
        }

        for (Property p :  this.properties.keySet()) {
            if (!propertyEquals(that, p)){
                return false;
            }
        }

        return true;
    }

    private boolean propertyEquals(CdoSnapshotState that, Property property){
        Object thisValue = this.getPropertyValue(property);
        Validate.conditionFulfilled(thisValue != null, "null value at CdoCnapshot property " + property.getName());

        Object thatValue = that.getPropertyValue(property);

        if (property.getType().isArray()){
            return Arrays.equals((Object[])thisValue, (Object[])thatValue);
        }

        return thisValue.equals(thatValue);
    }

    /**
     * List of properties with changed values (when comparing to previous state)
     */
    public List<Property> differentValues(CdoSnapshotState previous) {
        List<Property> different = new ArrayList<>();
        for (Property property : properties.keySet()) {

            if (!propertyEquals(previous, property)){
                different.add(property);
            }
        }
        return different;
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }
}
