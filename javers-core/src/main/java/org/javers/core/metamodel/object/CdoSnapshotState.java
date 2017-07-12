package org.javers.core.metamodel.object;

import org.javers.common.collections.Arrays;
import org.javers.common.collections.Defaults;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotState {
    private final Map<String, Object> properties;

    CdoSnapshotState(Map<String, Object> state) {
        Validate.argumentIsNotNull(state);
        this.properties = state;
    }

    int size(){
        return properties.size();
    }

    /**
     * returns default values for null primitives
     */
    public Object getPropertyValue(Property property) {
        Validate.argumentIsNotNull(property);
        Object val = properties.get(property.getName());
        if (val == null){
            return Defaults.defaultValue(property.getGenericType());
        }
        return val;
    }

    public Object getPropertyValue(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        return properties.get(propertyName);
    }


    boolean isNull(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        return !properties.containsKey(propertyName);
    }

    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(properties.keySet());
    }

    public <R> List<R> mapProperties(BiFunction<String, Object, R> mapper) {
        return Lists.transform(properties.entrySet(), entry -> mapper.apply(entry.getKey(), entry.getValue()));
    }

    public void forEachProperty(BiConsumer<String, Object> consumer) {
        properties.entrySet().forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CdoSnapshotState)) return false;

        CdoSnapshotState that = (CdoSnapshotState) o;

        if (this.properties.size() != that.properties.size()){
            return false;
        }

        for (String pName :  this.properties.keySet()) {
            if (!propertyEquals(that, pName)){
                return false;
            }
        }

        return true;
    }

    private boolean propertyEquals(CdoSnapshotState that, String propertyName){
        Object thisValue = this.getPropertyValue(propertyName);
        Object thatValue = that.getPropertyValue(propertyName);

        if (thisValue == null || thatValue == null){
            return false;
        }

        if (thisValue.getClass().isArray()){
            return Arrays.equals(thisValue, thatValue);
        }

        return thisValue.equals(thatValue);
    }

    /**
     * List of properties with changed values (when comparing to the previous state)
     */
    public List<String> differentValues(CdoSnapshotState previous) {
        List<String> different = new ArrayList<>();

        for (String propertyName : properties.keySet()) {
            if (previous.isNull(propertyName)){
                continue;
            }
            if (!propertyEquals(previous, propertyName)){
                different.add(propertyName);
            }
        }

        //add nullified
        different.addAll(Sets.xor(properties.keySet(), previous.properties.keySet()));

        return different;
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        String prefix = "";
        for (String propertyKey : getSortedPropertyKeys()) {
            stringBuilder.append(prefix).append(propertyKey).append(":").append(getPropertyValue(propertyKey));
            prefix = ", ";
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private List<String> getSortedPropertyKeys() {
        List<String> propertyList = new ArrayList<>(properties.keySet());
        Collections.sort(propertyList);
        return propertyList;
    }
}
