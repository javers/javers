package org.javers.core.metamodel.type;

import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.javers.common.collections.Lists.immutableCopyOf;
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

    ManagedClass(Class baseJavaClass, List<JaversProperty> managedProperties, List<JaversProperty> looksLikeId, ManagedPropertiesFilter managedPropertiesFilter) {
        argumentsAreNotNull(baseJavaClass, managedProperties, looksLikeId, managedPropertiesFilter);

        this.baseJavaClass = baseJavaClass;

        this.looksLikeId = immutableCopyOf(looksLikeId);
        this.managedPropertiesFilter = managedPropertiesFilter;
        this.managedProperties = immutableCopyOf(managedProperties);

        this.propertiesByName = new HashMap<>();
        managedProperties.forEach(property -> propertiesByName.put(property.getName(),property));
    }

    static ManagedClass unknown() {
        return new ManagedClass(Object.class, Collections.emptyList(), Collections.emptyList(), ManagedPropertiesFilter.empty());
    }

    ManagedClass createShallowReference(){
        return new ManagedClass(baseJavaClass, Collections.emptyList(), getLooksLikeId(), ManagedPropertiesFilter.empty());
    }

    ManagedPropertiesFilter getManagedPropertiesFilter() {
        return managedPropertiesFilter;
    }

    /**
     * Returns all managed properties, unmodifiable list
     */
    List<JaversProperty> getManagedProperties() {
        return managedProperties;
    }

    List<JaversProperty> getLooksLikeId() {
        return looksLikeId;
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

    /**
     * @throws JaversException PROPERTY_NOT_FOUND
     */
    List<JaversProperty> getProperties(List<String> withNames) {
        Validate.argumentIsNotNull(withNames);
        return withNames.stream().map(n -> getProperty(n)).collect(Collectors.toList());
    }

    boolean hasProperty(String propertyName) {
        return propertiesByName.containsKey(propertyName);
    }

    void forEachProperty(Consumer<JaversProperty> consumer) {
        managedProperties.forEach(p -> consumer.accept(p));
    }

    Class<?> getBaseJavaClass() {
        return baseJavaClass;
    }
}
