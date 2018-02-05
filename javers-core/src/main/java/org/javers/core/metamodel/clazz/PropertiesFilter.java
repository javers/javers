package org.javers.core.metamodel.clazz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

public class PropertiesFilter {
    private final List<String> includedProperties;
    private final List<String> ignoredProperties;

    public PropertiesFilter(List<String> includedProperties, List<String> ignoredProperties) {
        argumentsAreNotNull(ignoredProperties, includedProperties);
        this.includedProperties = new ArrayList<>(includedProperties);
        this.ignoredProperties = new ArrayList<>(ignoredProperties);
    }

    public List<String> getIgnoredProperties() {
        return Collections.unmodifiableList(ignoredProperties);
    }

    public List<String> getIncludedProperties() {
        return Collections.unmodifiableList(includedProperties);
    }
}
