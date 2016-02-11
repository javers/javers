package org.javers.core.metamodel.type;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.scanner.ClassScan;
import org.javers.core.metamodel.scanner.ClassScanner;
import org.slf4j.Logger;

import java.lang.reflect.Type;

import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author bartosz walacik
 */
class TypeFactory {
    private static final Logger logger = getLogger(TypeFactory.class);

    private final ClassScanner classScanner;
    private final ManagedClassFactory managedClassFactory;

    public TypeFactory(ClassScanner classScanner, TypeMapper typeMapper) {
        this.classScanner = classScanner;

        //Pico doesn't support cycles, so manual construction
        this.managedClassFactory = new ManagedClassFactory(classScanner, typeMapper);
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
        } else if (def instanceof IgnoredTypeDefinition) {
            return new IgnoredType(def.getBaseJavaClass());
        } else {
           throw new IllegalArgumentException("unsupported definition " + def.getClass().getSimpleName());
        }
    }

    EntityType createEntity(Class<?> javaType) {
        return createEntity(new EntityDefinition(javaType));
    }

    private ValueObjectType createValueObject(ValueObjectDefinition definition) {
        return new ValueObjectType(managedClassFactory.create(definition), definition.getTypeName());
    }

    private EntityType createEntity(EntityDefinition definition) {
        ManagedClass managedClass;
        if (definition.isShallowReference()){
            managedClass = managedClassFactory.createShallowReferenceManagedClass(definition);
        } else {
            managedClass = managedClassFactory.create(definition);
        }

        if (definition.hasCustomId()) {
            Property idProperty = managedClass.getProperty(definition.getIdPropertyName());
            return new EntityType(managedClass, Optional.of(idProperty), definition.getTypeName());
        } else {
            return new EntityType(managedClass, Optional.<Property>empty(), definition.getTypeName());
        }
    }

    JaversType infer(Type javaType, Optional<JaversType> prototype) {
        JaversType jType;

        if (prototype.isPresent()) {
            jType = spawnFromPrototype(javaType, prototype.get());
            logger.debug("javersType of {} inferred as {} from prototype {}",
                        javaType, jType.getClass().getSimpleName(), prototype.get());
        }
        else {
            jType = inferFromAnnotations(javaType);
            logger.debug("javersType of {} inferred as {}",
                        javaType, jType.getClass().getSimpleName());
        }

        return jType;
    }

    ValueType inferIdPropertyTypeAsValue(Type idPropertyGenericType) {
        logger.debug("javersType of [{}] inferred as ValueType, it's used as id-property type",
                idPropertyGenericType);

        return new ValueType(idPropertyGenericType);
    }

    private JaversType spawnFromPrototype(Type javaType, JaversType prototype) {
        Validate.argumentsAreNotNull(javaType, prototype);
        Class javaClass = extractClass(javaType);

        if (prototype instanceof ManagedType) {
            ManagedClass managedClass = managedClassFactory.create(javaClass);
            ClassScan scan = classScanner.scan(javaClass);
            return ((ManagedType) prototype).spawn(managedClass, scan.typeName());
        }
        else {
            return prototype.spawn(javaType); //delegate to simple constructor
        }
    }

    JaversType inferFromAnnotations(Type javaType) {
        Class javaClass = extractClass(javaType);
        ClassScan scan = classScanner.scan(javaClass);

        if (scan.hasValueAnn()){
            return create( new ValueDefinition(javaClass) );
        }

        if (scan.hasIgnoredAnn()){
            return create( new IgnoredTypeDefinition(javaClass) );
        }

        ClientsClassDefinitionBuilder builder;
        if (scan.hasIdProperty() || scan.hasEntityAnn()) {
            builder = EntityDefinitionBuilder.entityDefinition(javaClass);
            if (scan.hasShallowReferenceAnn()) {
                ((EntityDefinitionBuilder)builder).withShallowReference();
            }
        } else {
            builder = ValueObjectDefinitionBuilder.valueObjectDefinition(javaClass);
        }

        if (scan.typeName().isPresent()) {
            builder.withTypeName(scan.typeName().get());
        }

        return create(builder.build());
    }
}