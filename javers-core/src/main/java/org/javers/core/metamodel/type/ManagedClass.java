package org.javers.core.metamodel.type;

import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.javers.common.exception.JaversExceptionCode.PROPERTY_NOT_FOUND;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Decomposes a class into list of properties.
 * 
 * @author bartosz walacik
 */
class ManagedClass {
    private final Class<?> baseJavaClass;
    private final Map<String, JaversProperty> propertiesByName;
    private final List<JaversProperty> managedProperties;
    private final List<JaversProperty> looksLikeId;
    private final ManagedPropertiesFilter managedPropertiesFilter;

    ManagedClass(Class baseJavaClass, List<JaversProperty> allProperties, List<JaversProperty> looksLikeId, ManagedPropertiesFilter managedPropertiesFilter) {
        argumentsAreNotNull(baseJavaClass, allProperties, looksLikeId, managedPropertiesFilter);

        this.baseJavaClass = baseJavaClass;
        this.managedProperties = new ArrayList<>();
        this.propertiesByName = new HashMap<>();
        this.looksLikeId = looksLikeId;
        this.managedPropertiesFilter = managedPropertiesFilter;

        //TODO not sure why TransientAnn goes here, it should be encapsulated in ClassScan
        for (JaversProperty property : allProperties) {
            if (!property.hasTransientAnn()){
                this.managedProperties.add(property);
            }
            propertiesByName.put(property.getName(),property);
        }
    }

    ManagedClass createShallowReference(){
        return new ManagedClass(baseJavaClass, Collections.emptyList(), getLooksLikeId(), ManagedPropertiesFilter.empty());
    }

    ManagedPropertiesFilter getManagedPropertiesFilter() {
        return managedPropertiesFilter;
    }

    /**
     * returns all managed properties
     */
    List<JaversProperty> getManagedProperties() {
        return Collections.unmodifiableList(managedProperties);
    }

    List<JaversProperty> getLooksLikeId() {
        return Collections.unmodifiableList(looksLikeId);
    }

    Set<String> getPropertyNames(){
        return Collections.unmodifiableSet(propertiesByName.keySet());
    }

    /**
     * returns managed properties subset
     */
    List<JaversProperty> getManagedProperties(Predicate<JaversProperty> query) {
        return Lists.positiveFilter(managedProperties, query);
    }

    /**
     * finds property by name (managed or withTransientAnn)
     *
     * @throws JaversException PROPERTY_NOT_FOUND
     */
    JaversProperty getProperty(String withName) {
        Validate.argumentIsNotNull(withName);
        if (!propertiesByName.containsKey(withName)){
            throw new JaversException(PROPERTY_NOT_FOUND, withName, baseJavaClass.getName());
        }
        return propertiesByName.get(withName);
    }

    void forEachProperty(Consumer<JaversProperty> consumer) {
        managedProperties.forEach(p -> consumer.accept(p));
    }

    Class<?> getBaseJavaClass() {
        return baseJavaClass;
    }
}
