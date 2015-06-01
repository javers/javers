package org.javers.core.metamodel.clazz;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EntityType;

import java.util.Collections;
import java.util.List;

/**
 * Recipe for {@link EntityType}
 *
 * @author bartosz walacik
 */
public class EntityDefinition  extends ClientsClassDefinition {
    private final IdPropertyDefinition idPropertyDefinition;

    /**
     * Creates Entity with Id-property selected by @Id annotation
     */
    public EntityDefinition(Class<?> clazz) {
        super(clazz);
        this.idPropertyDefinition = null;
    }

    private EntityDefinition(Class<?> clazz, IdPropertyDefinition idPropertyDefinition, List<String> ignoredProperties) {
        super(clazz, ignoredProperties);
        this.idPropertyDefinition = idPropertyDefinition;
    }

    /**
     * Creates Entity with Id-property selected explicitly by name
     */
    public EntityDefinition(Class<?> clazz, String idPropertyName){
        this(clazz, new IdPropertyDefinition(idPropertyName), Collections.EMPTY_LIST);

    }

    /**
     * Creates Entity with Id-property selected explicitly
     */
    public EntityDefinition(Class<?> clazz, Property idProperty){
        this(clazz, new IdPropertyDefinition(idProperty), Collections.EMPTY_LIST);

    }

    /**
     * Creates Entity with Id-property selected explicitly by name, ignores given properties
     */
    public EntityDefinition(Class<?> clazz, String idPropertyName, List<String> ignoredProperties) {
        this(clazz, new IdPropertyDefinition(idPropertyName), ignoredProperties);

    }

    public boolean hasCustomId() {
        return idPropertyDefinition != null;
    }

    public boolean hasCustomIdDefinedByName() {
        return hasCustomId() && idPropertyDefinition.propertyName != null;
    }

    public String getIdPropertyName() {
        return idPropertyDefinition.propertyName;
    }

    public Property getIdProperty(){
        return idPropertyDefinition.property;
    }

    private static class IdPropertyDefinition{
        private String propertyName;
        private Property property;

        public IdPropertyDefinition(Property property) {
            Validate.argumentIsNotNull(property);
            this.property = property;
        }

        public IdPropertyDefinition(String propertyName) {
            Validate.argumentIsNotNull(propertyName);
            this.propertyName = propertyName;
        }
    }
}
