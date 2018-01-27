package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.clazz.PropertiesFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ManagedPropertiesFilter {
    private final Set<JaversProperty> includedProperties;
    private final Set<JaversProperty> ignoredProperties;

    static ManagedPropertiesFilter empty() {
        return new ManagedPropertiesFilter();
    }

    ManagedPropertiesFilter(Class<?> baseJavaClass, List<JaversProperty> allSourceProperties, PropertiesFilter propertiesFilter) {

        this.includedProperties = filter(allSourceProperties, propertiesFilter.getIncludedProperties(), baseJavaClass);
        this.includedProperties.addAll(allSourceProperties.stream().filter(p -> p.isHasIncludedAnn()).collect(Collectors.toSet()));

        this.ignoredProperties = filter(allSourceProperties, propertiesFilter.getIgnoredProperties(), baseJavaClass);
        this.ignoredProperties.addAll(allSourceProperties.stream().filter(p -> p.hasTransientAnn()).collect(Collectors.toSet()));

        if (this.ignoredProperties.size() > 0  && this.includedProperties.size() > 0) {
            throw new JaversException(JaversExceptionCode.IGNORED_AND_INCLUDED_PROPERTIES_MIX, baseJavaClass.getSimpleName());
        }
    }

    private ManagedPropertiesFilter() {
        this.includedProperties = Collections.emptySet();
        this.ignoredProperties = Collections.emptySet();
    }

    List<JaversProperty> filterProperties(List<JaversProperty> allProperties){
        if (hasIncludedProperties()) {
            return new ArrayList<>(includedProperties);
        }

        if (hasIgnoredProperties()) {
            return allProperties.stream().filter(it -> !ignoredProperties.contains(it)).collect(Collectors.toList());
        }

        return allProperties;
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
