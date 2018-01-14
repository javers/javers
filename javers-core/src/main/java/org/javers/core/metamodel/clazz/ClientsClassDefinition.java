package org.javers.core.metamodel.clazz;

import java.util.Optional;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.CustomType;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.javers.core.metamodel.type.ValueType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Recipe for {@link EntityType}, {@link ValueObjectType}, {@link ValueType} or {@link CustomType}
 *
 * @author bartosz walacik
 */
public abstract class ClientsClassDefinition {
    private final List<String> whitelistedProperties;
    private final Class<?> baseJavaClass;
    private final List<String> ignoredProperties;
    private final Optional<String> typeName;

    ClientsClassDefinition(Class<?> baseJavaClass) {
        this(baseJavaClass, emptyList(), Optional.empty(), emptyList());
    }

    ClientsClassDefinition(Class<?> baseJavaClass, List<String> ignoredProperties) {
        this(baseJavaClass, ignoredProperties, Optional.empty(), emptyList());
    }

    ClientsClassDefinition(ClientsClassDefinitionBuilder builder) {
        this(builder.getClazz(), builder.getIgnoredProperties(), builder.getTypeName(), builder.getWhitelistedProperties());
    }

    private ClientsClassDefinition(Class<?> baseJavaClass, List<String> ignoredProperties, Optional<String> typeName, List<String> whitelistedProperties) {
        argumentsAreNotNull(baseJavaClass, typeName, ignoredProperties, whitelistedProperties);

        Validate.argumentCheck(!(whitelistedProperties.size() > 0 && ignoredProperties.size() > 0),
                "Can't create ClientsClassDefinition for " + baseJavaClass.getSimpleName() +
                ", you can't define both ignored and whitelisted properties");

        this.baseJavaClass = baseJavaClass;
        this.ignoredProperties = new ArrayList<>(ignoredProperties);
        this.typeName = typeName;
        this.whitelistedProperties = new ArrayList<>(whitelistedProperties);
    }

    public Class<?> getBaseJavaClass() {
        return baseJavaClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o || getClass() != o.getClass()) {
            return false;
        }

        ClientsClassDefinition that = (ClientsClassDefinition) o;

        return baseJavaClass.equals(that.baseJavaClass);
    }

    @Override
    public int hashCode() {
        return baseJavaClass.hashCode();
    }

    /**
     * List of properties to be ignored by JaVers.
     * Properties can be also ignored with the {@link org.javers.core.metamodel.annotation.DiffIgnore}
     * annotation.
     */
    public List<String> getIgnoredProperties() {
        return Collections.unmodifiableList(ignoredProperties);
    }

    public boolean hasIgnoredProperties() {
        return !ignoredProperties.isEmpty();
    }

    public boolean hasWhitelistedProperties() {
        return !whitelistedProperties.isEmpty();
    }

    /**
     * If whitelisted properties list is defined, only those props are
     * visible for JaVers, and the rest is ignored.
     * <br/>
     *
     * Whitelisted props can be defined only if ignored properties are not defined.
     */
    public List<String> getWhitelistedProperties() {
        return Collections.unmodifiableList(whitelistedProperties);
    }

    public Optional<String> getTypeName() {
        return typeName;
    }

    public boolean hasTypeName(){
        return typeName.isPresent();
    }

}
