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

    /**
     * List of class properties to be ignored by JaVers.
     * Properties can be also ignored with the {@link org.javers.core.metamodel.annotation.DiffIgnore}
     * annotation.
     * <br/><br/>
     *
     * Ignored properties can be defined only if included properties are not defined.
     */
    public List<String> getIgnoredProperties() {
        return Collections.unmodifiableList(ignoredProperties);
    }

    /**
     * If included properties list is defined for a class,
     * only those props are visible for JaVers, and the rest is ignored.
     * <br/><br/>
     *
     * Included properties can be defined only if ignored properties are not defined.
     */
    public List<String> getIncludedProperties() {
        return Collections.unmodifiableList(includedProperties);
    }
}
