package org.javers.core.metamodel.clazz;

import java.util.List;

import static org.javers.common.collections.Lists.immutableCopyOf;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

public class PropertiesFilter {
    private final List<String> includedProperties;
    private final List<String> ignoredProperties;
    private final List<String> shallowProperties;

    public PropertiesFilter(List<String> includedProperties, List<String> ignoredProperties, List<String> shallowProperties) {
        argumentsAreNotNull(ignoredProperties, includedProperties, shallowProperties);
        this.includedProperties = immutableCopyOf(includedProperties);
        this.ignoredProperties = immutableCopyOf(ignoredProperties);
        this.shallowProperties = shallowProperties;
    }

    public List<String> getIgnoredProperties() {
        return ignoredProperties;
    }

    public List<String> getIncludedProperties() {
        return includedProperties;
    }

    public List<String> getShallowProperties() {
        return shallowProperties;
    }
}
