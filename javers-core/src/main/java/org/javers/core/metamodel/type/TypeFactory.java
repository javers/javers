package org.javers.core.metamodel.type;

import java.util.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.*;
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
    private final EntityTypeFactory entityTypeFactory;

    public TypeFactory(ClassScanner classScanner, TypeMapper typeMapper) {
        this.classScanner = classScanner;

        //Pico doesn't support cycles, so manual construction
        this.managedClassFactory = new ManagedClassFactory(typeMapper);

        this.entityTypeFactory = new EntityTypeFactory(managedClassFactory);
    }

    JaversType create(ClientsClassDefinition def) {
        return create(def, classScanner.scan(def.getBaseJavaClass()));
    }

    JaversType create(ClientsClassDefinition def, ClassScan scan) {
        if (def instanceof CustomDefinition) {
            return new CustomType(def.getBaseJavaClass());
        } else if (def instanceof EntityDefinition) {
            return entityTypeFactory.createEntity((EntityDefinition) def, scan);
        } else if (def instanceof ValueObjectDefinition){
            return createValueObject((ValueObjectDefinition) def, scan);
        } else if (def instanceof ValueDefinition) {
            ValueDefinition valueDefinition = (ValueDefinition) def;
            return valueDefinition.getComparator()
                    .map(comparator -> new ValueType(valueDefinition.getBaseJavaClass(), comparator))
                    .orElse(new ValueType(valueDefinition.getBaseJavaClass()));
        } else if (def instanceof IgnoredTypeDefinition) {
            return new IgnoredType(def.getBaseJavaClass());
        } else {
           throw new IllegalArgumentException("unsupported definition " + def.getClass().getSimpleName());
        }
    }

    private ValueObjectType createValueObject(ValueObjectDefinition definition, ClassScan scan) {
        return new ValueObjectType(managedClassFactory.create(definition, scan), definition.getTypeName());
    }

    JaversType infer(Type javaType, Optional<JaversType> prototype) {
        JaversType jType;

        if (prototype.isPresent()) {
            jType = spawnFromPrototype(javaType, prototype.get());
            logger.debug("javersType of {} spawned as {} from prototype {}",
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
        logger.debug("javersType of {} inferred as ValueType, it's used as id-property type",
                idPropertyGenericType);

        return new ValueType(idPropertyGenericType);
    }

    private JaversType spawnFromPrototype(Type javaType, JaversType prototype) {
        Validate.argumentsAreNotNull(javaType, prototype);
        Class javaClass = extractClass(javaType);

        if (prototype instanceof ManagedType) {
            ClassScan scan = classScanner.scan(javaClass);
            ManagedClass managedClass = managedClassFactory.create(javaClass, scan);
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
            return create( new ValueDefinition(javaClass), scan);
        }

        if (scan.hasIgnoredAnn()){
            return create( new IgnoredTypeDefinition(javaClass), scan);
        }

        if (scan.hasValueObjectAnn()) {
            return create(ValueObjectDefinitionBuilder.valueObjectDefinition(javaClass).build(), scan);
        }

        ClientsClassDefinitionBuilder builder;
        if (scan.hasShallowReferenceAnn()) {
            builder = EntityDefinitionBuilder.entityDefinition(javaClass).withShallowReference();
        } else
        if (scan.hasEntityAnn() || scan.hasIdProperty()) {
            builder = EntityDefinitionBuilder.entityDefinition(javaClass);
        } else {
            builder = ValueObjectDefinitionBuilder.valueObjectDefinition(javaClass);
        }

        if (scan.typeName().isPresent()) {
            builder.withTypeName(scan.typeName().get());
        }

        return create(builder.build(), scan);
    }
}
