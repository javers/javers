package org.javers.core.metamodel.clazz;

import java.util.List;

import static org.javers.common.collections.Lists.immutableCopyOf;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

public class PropertiesFilter {
    private final List<String> includedProperties;
    private final List<String> ignoredProperties;

    public PropertiesFilter(List<String> includedProperties, List<String> ignoredProperties) {
        argumentsAreNotNull(ignoredProperties, includedProperties);
        this.includedProperties = immutableCopyOf(includedProperties);
        this.ignoredProperties = immutableCopyOf(ignoredProperties);
    }

    public List<String> getIgnoredProperties() {
        return ignoredProperties;
    }

    public List<String> getIncludedProperties() {
        return includedProperties;
    }
}
