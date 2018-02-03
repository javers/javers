package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Lists;
import java.util.Optional;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

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
     * @see #withIgnoredProperties(List)
     */
    public T withIgnoredProperties(String... ignoredProperties) {
        withIgnoredProperties(Lists.asList(ignoredProperties));
        return (T) this;
    }

    /**
     * List of class properties to be ignored by JaVers.
     * <br/><br/>
     *
     * Properties can be also ignored with the {@link DiffIgnore} annotation.
     * <br/><br/>
     *
     * You can either specify includedProperties or ignoredProperties, not both.
     *
     * @see DiffIgnore
     * @throws IllegalArgumentException If includedProperties was already set.
     */
    public T withIgnoredProperties(List<String> ignoredProperties) {
        Validate.argumentIsNotNull(ignoredProperties);
        if (includedProperties.size() > 0) {
            throw new JaversException(JaversExceptionCode.IGNORED_AND_INCLUDED_PROPERTIES_MIX, clazz.getSimpleName());
        }
        this.ignoredProperties = ignoredProperties;
        return (T) this;
    }

    /**
     * If included properties are defined for a class,
     * only these properties are visible for JaVers, and the rest is ignored.
     * <br/><br/>
     *
     * Properties can be also included with the {@link DiffInclude} annotation.
     * <br/><br/>
     *
     * You can either specify includedProperties or ignoredProperties, not both.
     *
     * @throws JaversException If ignoredProperties was already set
     */
    public T withIncludedProperties(List<String> includedProperties) {
        Validate.argumentIsNotNull(includedProperties);
        if (ignoredProperties.size() > 0) {
            throw new JaversException(JaversExceptionCode.IGNORED_AND_INCLUDED_PROPERTIES_MIX, clazz.getSimpleName());
        }
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
