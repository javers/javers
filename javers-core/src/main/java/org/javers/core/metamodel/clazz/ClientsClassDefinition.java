package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
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
        this(new ClientsClassDefinitionBuilder(baseJavaClass));
    }

    ClientsClassDefinition(Class<?> clazz, List<String> ignoredProperties) {
        this(new ClientsClassDefinitionBuilder(clazz)
                .withIgnoredProperties(ignoredProperties));
    }

    ClientsClassDefinition(ClientsClassDefinitionBuilder builder) {
        argumentsAreNotNull(builder);
        this.baseJavaClass = builder.clazz;
        this.ignoredProperties = new ArrayList<>(builder.ignoredProperties);
        this.typeName = builder.typeName;
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

    public static class ClientsClassDefinitionBuilder<T extends ClientsClassDefinitionBuilder> {
        private Class<?> clazz;
        private List<String> ignoredProperties = Collections.EMPTY_LIST;
        private Optional<String> typeName = Optional.empty();

        ClientsClassDefinitionBuilder(Class<?> clazz) {
            this.clazz = clazz;
        }

        public T withIgnoredProperties(String... ignoredProperties) {
            withIgnoredProperties(Lists.asList(ignoredProperties));
            return (T) this;
        }

        public T withIgnoredProperties(List<String> ignoredProperties) {
            Validate.argumentIsNotNull(ignoredProperties);
            this.ignoredProperties = ignoredProperties;
            return (T) this;
        }

        public T withTypeName(String typeName) {
            Validate.argumentIsNotNull(typeName);
            this.typeName = Optional.of(typeName);
            return (T)this;
        }

        public ClientsClassDefinition build() {
            throw new RuntimeException("not implemented");
        }
    }
}
