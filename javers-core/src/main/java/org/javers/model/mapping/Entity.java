package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.EntityReferenceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Class in client's domain model
 * <br/>
 *
 * immutable
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Entity<S> extends ManagedClass<S> {

    private final Property idProperty;
    private final List<Property> properties;


    public Entity(Class<S> sourceClass, List<Property> properties) {
        super(sourceClass);
        argumentIsNotNull(properties);
        this.properties = properties;
        this.idProperty = findIdProperty();
    }

    private Property findIdProperty() {
        for (Property p : properties) {
            if (p.isId()) {
                return p;
            }
        }
        throw new JaversException(JaversExceptionCode.ENTITY_WITHOUT_ID,sourceClass.getName());
    }

    /**
     * @param cdo instance of {@link #getSourceClass()}
     * @return returns ID of given cdo
     */
    public Object getCdoIdOf(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        Validate.argumentCheck(getSourceClass().isInstance(cdo),
                               "expected instance of "+getSourceClass().getName()+", got instance of "+cdo.getClass().getName());
        return getIdProperty().get(cdo);
    }

    public boolean isInstance(Object cdo) {
        return getSourceClass().isInstance(cdo);
    }

    public Property getIdProperty() {
        return idProperty;
    }

    /**
     * @return list of {@link org.javers.model.mapping.type.EntityReferenceType} properties
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

    public List<Property> getMultiReferences() {
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