package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Lists;
import java.util.Optional;
import org.javers.common.validation.Validate;

import java.util.Collections;
import java.util.List;

/**
 * @since 1.4
 * @author bartosz.walacik
 */
public abstract class ClientsClassDefinitionBuilder<T extends ClientsClassDefinitionBuilder> {
    private Class<?> clazz;
    private List<String> ignoredProperties = Collections.emptyList();
    private List<String> includedProperties = Collections.emptyList();
    private Optional<String> typeName = Optional.empty();

    ClientsClassDefinitionBuilder(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * See {@link ClientsClassDefinition#getIgnoredProperties()}
     * @throws IllegalArgumentException If includedProperties was already set. You can either specify includedProperties or ignoredProperties, not both.
     */
    public T withIgnoredProperties(String... ignoredProperties) {
        withIgnoredProperties(Lists.asList(ignoredProperties));
        return (T) this;
    }

    /**
     * See {@link ClientsClassDefinition#getIgnoredProperties()}
     * @throws IllegalArgumentException If includedProperties was already set. You can either specify includedProperties or ignoredProperties, not both.
     */
    public T withIgnoredProperties(List<String> ignoredProperties) {
        Validate.argumentCheck(this.includedProperties.size() == 0, "includedProperties already set. You can either specify includedProperties or ignoredProperties, not both.");
        Validate.argumentIsNotNull(ignoredProperties);
        this.ignoredProperties = ignoredProperties;
        return (T) this;
    }

    /**
     * See {@link ClientsClassDefinition#getIncludedProperties()}
     * @throws IllegalArgumentException If ignoredProperties was already set. You can either specify includedProperties or ignoredProperties, not both.
     */
    public T withIncludedProperties(List<String> includedProperties) {
        Validate.argumentCheck(this.ignoredProperties.size() == 0, "ignoredProperties already set. You can either specify includedProperties or ignoredProperties, not both.");
        Validate.argumentIsNotNull(includedProperties);
        this.includedProperties = includedProperties;
        return (T) this;
    }

    public T withTypeName(Optional<String> typeName) {
        Validate.argumentIsNotNull(typeName);
        this.typeName = typeName;
        return (T) this;
    }

    public T withTypeName(String typeName) {
        return withTypeName(Optional.ofNullable(typeName));
    }

    public ClientsClassDefinition build() {
        throw new RuntimeException("not implemented");
    }

    Class<?> getClazz() {
        return clazz;
    }

    List<String> getIgnoredProperties() {
        return ignoredProperties;
    }

    List<String> getIncludedProperties() {
        return includedProperties;
    }

    Optional<String> getTypeName() {
        return typeName;
    }

}
