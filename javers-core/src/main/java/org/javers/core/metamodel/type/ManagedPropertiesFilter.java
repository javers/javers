package org.javers.core.metamodel.type;

import java.util.HashSet;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.clazz.PropertiesFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ManagedPropertiesFilter {
    private static final Logger logger = LoggerFactory.getLogger(ManagedPropertiesFilter.class);
    private final Set<JaversProperty> includedProperties;
    private final Set<JaversProperty> ignoredProperties;
    private final Set<String> shallowProperties;

    static ManagedPropertiesFilter empty() {
        return new ManagedPropertiesFilter();
    }

    ManagedPropertiesFilter(Class<?> baseJavaClass, List<JaversProperty> allSourceProperties, PropertiesFilter propertiesFilter) {
        this.includedProperties = filter(allSourceProperties, propertiesFilter.getIncludedProperties(), baseJavaClass);
        this.includedProperties.addAll(allSourceProperties.stream().filter(p -> p.isHasIncludedAnn()).collect(Collectors.toSet()));

        if (this.includedProperties.size() > 0) {
            logger.debug("Included properties have been provided and thus any @Transient or @DiffIgnore annotation will be disregarded for class " + baseJavaClass.getName());
            this.ignoredProperties = Collections.emptySet();
        } else {
            this.ignoredProperties = filter(allSourceProperties, propertiesFilter.getIgnoredProperties(), baseJavaClass);
            this.ignoredProperties.addAll(allSourceProperties.stream().filter(p -> p.hasTransientAnn()).collect(Collectors.toSet()));
        }

        this.shallowProperties = new HashSet<>(propertiesFilter.getShallowProperties());
    }

    private ManagedPropertiesFilter() {
        this.includedProperties = Collections.emptySet();
        this.ignoredProperties = Collections.emptySet();
        this.shallowProperties = Collections.emptySet();
    }

    List<JaversProperty> filterProperties(List<JaversProperty> allProperties){
        List<JaversProperty> baseListOfProperties;
        if (hasIncludedProperties()) {
            baseListOfProperties = new ArrayList<>(includedProperties);
        } else if (hasIgnoredProperties()) {
            baseListOfProperties = allProperties.stream().filter(it -> !ignoredProperties.contains(it)).collect(Collectors.toList());
        }
        else {
            baseListOfProperties = allProperties;
        }
        return applyShallowPropertiesConfiguration(baseListOfProperties);
    }

    private List<JaversProperty> applyShallowPropertiesConfiguration(List<JaversProperty> allProperties) {
        return allProperties.stream()
            .map(it -> shallowProperties.contains(it.getName()) && !it.isShallowReference()
                    ? it.copyAsShallowReference()
                    : it)
            .collect(Collectors.toList());
    }

    boolean hasIgnoredProperties() {
        return !ignoredProperties.isEmpty();
    }

    boolean hasIncludedProperties() {
        return !includedProperties.isEmpty();
    }

    private Set<JaversProperty> filter(List<JaversProperty> allProperties, List<String> propertyNames, Class<?> baseJavaClass) {
        return propertyNames.stream()
            .map(p -> allProperties.stream()
                    .filter(jp -> jp.getName().equals(p))
                    .findFirst()
                    .orElseThrow(() -> new JaversException(JaversExceptionCode.PROPERTY_NOT_FOUND, p, baseJavaClass.getName())))
            .collect(Collectors.toSet());
    }
}
