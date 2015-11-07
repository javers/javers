package org.javers.core.metamodel.type;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.annotation.ClassAnnotationsScan;
import org.javers.core.metamodel.annotation.ClassAnnotationsScanner;
import org.javers.core.metamodel.clazz.*;
import org.javers.core.metamodel.clazz.ClientsClassDefinition.ClientsClassDefinitionBuilder;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.PropertyScanner;
import org.slf4j.Logger;

import java.lang.reflect.Type;

import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.javers.core.metamodel.clazz.EntityDefinition.EntityDefinitionBuilder.entityDefinition;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author bartosz walacik
 */
public class TypeFactory {
    private static final Logger logger = getLogger(TypeFactory.class);

    private PropertyScanner propertyScanner;
    private ClassAnnotationsScanner classAnnotationsScanner;
    private final ManagedClassFactory managedClassFactory;

    public TypeFactory(ManagedClassFactory managedClassFactory, ClassAnnotationsScanner classAnnotationsScanner, PropertyScanner propertyScanner) {
        this.managedClassFactory = managedClassFactory;
        this.classAnnotationsScanner = classAnnotationsScanner;
        this.propertyScanner = propertyScanner;
    }

    JaversType create(ClientsClassDefinition def) {
        if (def instanceof CustomDefinition) {
            return new CustomType(def.getBaseJavaClass());
        } else if (def instanceof EntityDefinition) {
            return createEntity((EntityDefinition) def);
        } else if (def instanceof ValueObjectDefinition){
            return createValueObject((ValueObjectDefinition) def);
        } else if (def instanceof ValueDefinition) {
            return new ValueType(def.getBaseJavaClass());
        } else {
           throw new IllegalArgumentException("unsupported definition " + def.getClass().getSimpleName());
        }
    }

    EntityType createEntity(Class<?> javaType) {
        return (EntityType) create(new EntityDefinition(javaType));
    }

    ValueObjectType createValueObject(ValueObjectDefinition definition) {
        ManagedClass managedClass = managedClassFactory.create(definition);
        return new ValueObjectType(managedClass, definition.getTypeName());
    }

    EntityType createEntity(EntityDefinition definition) {
        ManagedClass managedClass = managedClassFactory.create(definition);

        if (definition.hasCustomId()) {
            Property idProperty = managedClass.getProperty(definition.getIdPropertyName());
            return new EntityType(managedClass, Optional.of(idProperty), definition.getTypeName());
        } else {
            return new EntityType(managedClass, Optional.<Property>empty(), definition.getTypeName());
        }

    }
    JaversType infer(Type javaType, Optional<JaversType> prototype){
        JaversType jType;

        if (prototype.isPresent()) {
            jType = spawnFromPrototype(javaType, prototype.get());
            logger.info("javersType of {} inferred as {} from prototype {}",
                        javaType, jType.getClass().getSimpleName(), prototype.get());
        }
        else {
            jType = inferFromAnnotations(javaType);
            logger.info("javersType of {} inferred as {}",
                        javaType, jType.getClass().getSimpleName());
        }

        return jType;
    }

    ValueType inferIdPropertyTypeAsValue(Type idPropertyGenericType) {
        logger.info("javersType of [{}] inferred as ValueType, it's used as id-property type",
                idPropertyGenericType);

        return new ValueType(idPropertyGenericType);
    }

    private JaversType spawnFromPrototype(Type javaType, JaversType prototype) {
        Validate.argumentsAreNotNull(javaType, prototype);
        Class javaClass = extractClass(javaType);

        if (prototype instanceof ManagedType) {
            ManagedClass managedClass = managedClassFactory.create(javaClass);
            return ((ManagedType)prototype).spawn(managedClass);
        }
        else {
            return prototype.spawn(javaType); //delegate to simple constructor
        }
    }

    JaversType inferFromAnnotations(Type javaType) {
        Class javaClass = extractClass(javaType);
        ClassAnnotationsScan scan = classAnnotationsScanner.scan(javaClass);

        if (scan.hasValue()){
            return create( new ValueDefinition(javaClass) );
        }

        ClientsClassDefinitionBuilder builder;

        if (hasIdProperty(javaClass) || scan.hasEntity()){
            builder = entityDefinition(javaClass);
        } else {
            builder = ValueObjectDefinition.ValueObjectDefinitionBuilder.valueObjectDefinition(javaClass);
        }

        if (scan.typeName().isPresent()) {
            builder.withTypeName(scan.typeName().get());
        }

        return create(builder.build());
    }

    private boolean hasIdProperty(Class<?> javaClass) {
        for (Property property : propertyScanner.scan(javaClass))  {
            if (property.looksLikeId()) {
                return true;
            }
        }
        return false;
    }
}

