package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Optional;
import org.javers.core.metamodel.type.CustomType;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.javers.core.metamodel.type.ValueType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Recipe for {@link EntityType}, {@link ValueObjectType}, {@link ValueType} or {@link CustomType}
 *
 * @author bartosz walacik
 */
public abstract class ClientsClassDefinition {
    private final Class<?> baseJavaClass;
    private final List<String> ignoredProperties;
    private final Optional<String> typeName;

    ClientsClassDefinition(Class<?> baseJavaClass) {
        this(baseJavaClass, Collections.<String>emptyList(), Optional.<String>empty());
    }

    ClientsClassDefinition(Class<?> baseJavaClass, List<String> ignoredProperties) {
        this(baseJavaClass, ignoredProperties, Optional.<String>empty());
    }

    ClientsClassDefinition(ClientsClassDefinitionBuilder builder) {
        this(builder.getClazz(), builder.getIgnoredProperties(), builder.getTypeName());
    }

    private ClientsClassDefinition(Class<?> baseJavaClass, List<String> ignoredProperties, Optional<String> typeName) {
        argumentsAreNotNull(baseJavaClass, typeName, ignoredProperties);
        this.baseJavaClass = baseJavaClass;
        this.ignoredProperties = new ArrayList<>(ignoredProperties);
        this.typeName = typeName;
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

    public List<String> getIgnoredProperties() {
        return Collections.unmodifiableList(ignoredProperties);
    }

    public Optional<String> getTypeName() {
        return typeName;
    }

    public boolean hasTypeName(){
        return typeName.isPresent();
    }

}
