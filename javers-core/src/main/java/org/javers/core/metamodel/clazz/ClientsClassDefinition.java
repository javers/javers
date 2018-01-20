package org.javers.core.metamodel.clazz;

import java.util.Optional;

import org.javers.core.metamodel.type.CustomType;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.javers.core.metamodel.type.ValueType;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.javers.common.validation.Validate.argumentCheck;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Recipe for {@link EntityType}, {@link ValueObjectType}, {@link ValueType} or {@link CustomType}
 *
 * @author bartosz walacik
 */
public abstract class ClientsClassDefinition {
    private final Class<?> baseJavaClass;
    private final Optional<String> typeName;
    private final PropertiesFilter propertiesFilter;

    ClientsClassDefinition(Class<?> baseJavaClass) {
        this(baseJavaClass, emptyList(), Optional.empty(), emptyList());
    }

    ClientsClassDefinition(Class<?> baseJavaClass, List<String> ignoredProperties) {
        this(baseJavaClass, ignoredProperties, Optional.empty(), emptyList());
    }

    ClientsClassDefinition(ClientsClassDefinitionBuilder builder) {
        this(builder.getClazz(), builder.getIgnoredProperties(), builder.getTypeName(), builder.getIncludedProperties());
    }

    private ClientsClassDefinition(Class<?> baseJavaClass, List<String> ignoredProperties, Optional<String> typeName, List<String> includedProperties) {
        argumentsAreNotNull(baseJavaClass, typeName, ignoredProperties, includedProperties);

        argumentCheck(!(includedProperties.size() > 0 && ignoredProperties.size() > 0),
                "Can't create ClientsClassDefinition for " + baseJavaClass.getSimpleName() +
                ", you can't define both ignored and included properties");

        this.baseJavaClass = baseJavaClass;
        this.typeName = typeName;
        this.propertiesFilter = new PropertiesFilter(includedProperties, ignoredProperties);
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

    public Optional<String> getTypeName() {
        return typeName;
    }

    public boolean hasTypeName(){
        return typeName.isPresent();
    }

    public PropertiesFilter getPropertiesFilter() {
        return propertiesFilter;
    }
}
