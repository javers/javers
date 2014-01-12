package org.javers.model.mapping;

import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.EntityReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class in client's domain model. Has list of mutable properties but no unique identifier.
 * Two valueObjects are compared property by property.
 * <p/>
 * Example:
 * <pre>
 *     class Address {
 *         private String city;
 *         private String street;
 *         private String zip;
 *         ...
 *     }
 * </pre>
 *
 * @author bartosz walacik
 */
public class ValueObject extends ManagedClass {
    private static final Logger logger = LoggerFactory.getLogger(ValueObject.class);
    protected final List<Property> properties;

    public ValueObject(Class sourceClass, List<Property> properties) {
        super(sourceClass);
        this.properties = properties;
    }

    @Deprecated
    public ValueObject(Class sourceClass) {
        super(sourceClass);
        this.properties = Collections.EMPTY_LIST;
    }

    /**
     * @return list of {@link EntityReferenceType} properties
     */
    public List<Property> getSingleReferences() {
        List<Property> refProperties = new ArrayList<>();

        for (Property property : properties) {
            if (property.getType() instanceof EntityReferenceType){
                refProperties.add(property);
            }
        }
        return refProperties;
    }

    public List<Property> getCollectionTypeProperties() {
        List<Property> refProperties = new ArrayList<>();

        for (Property property : properties) {
            if (property.getType() instanceof CollectionType){
                refProperties.add(property);
            }
        }
        return refProperties;
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public Property getProperty(String withName) {
        Property found = null;
        for (Property property : properties) {
            if (property.getName().equals(withName)) {
                found = property;
            }
        }
        return found;
    }
}
