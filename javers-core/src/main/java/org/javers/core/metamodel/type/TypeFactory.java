package org.javers.core.metamodel.type;

import java.lang.reflect.TypeVariable;
import java.util.*;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.*;
import org.javers.core.metamodel.scanner.ClassScan;
import org.javers.core.metamodel.scanner.ClassScanner;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.function.Supplier;

import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.javers.core.metamodel.clazz.EntityDefinitionBuilder.entityDefinition;
import static org.javers.core.metamodel.clazz.ValueObjectDefinitionBuilder.valueObjectDefinition;
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
        } else if (def instanceof ValueObjectDefinition) {
            return createValueObject((ValueObjectDefinition) def, scan);
        } else if (def instanceof ValueDefinition) {
            ValueDefinition valueDefinition = (ValueDefinition) def;
            return new ValueType(valueDefinition.getBaseJavaClass(),
                    valueDefinition.getComparator(),
                    valueDefinition.getToStringFunction());
        } else if (def instanceof IgnoredTypeDefinition) {
            return new IgnoredType(def.getBaseJavaClass());
        } else {
            throw new IllegalArgumentException("unsupported definition " + def.getClass().getSimpleName());
        }
    }

    private ValueObjectType createValueObject(ValueObjectDefinition definition, ClassScan scan) {
        return new ValueObjectType(managedClassFactory.create(definition, scan), definition.getTypeName(), definition.isDefault());
    }

    JaversType infer(Type javaType) {
        return infer(javaType, Optional.empty());
    }

    JaversType infer(Type javaType, Optional<JaversType> prototype) {
        JavaRichType javaRichType = new JavaRichType(javaType);

        if (prototype.isPresent()) {
            JaversType jType = spawnFromPrototype(javaRichType, prototype.get());
            logger.debug("javersType of {} spawned as {} from prototype {}",
                    javaRichType.getSimpleName(), jType.getClass().getSimpleName(), prototype.get());
            return jType;
        }

        return inferFromAnnotations(javaRichType).map(jType -> {
            logger.debug("javersType of {} inferred from annotations as {}",
                    javaRichType.getSimpleName(), jType.getClass().getSimpleName());
            return jType;
        }).orElseGet(() -> {
            logger.debug("javersType of {} defaulted to ValueObjectType", javaRichType.getSimpleName());
            return createDefaultType(javaRichType);
        });
    }

    boolean inferredAsEntity(Type javaType) {
        if (javaType instanceof TypeVariable) {
            return false;
        }
        JavaRichType t = new JavaRichType(javaType);
        return t.getScan().hasEntityAnn() || t.getScan().hasIdProperty();
    }

    JaversType inferIdPropertyTypeAsValue(Type idPropertyGenericType) {
        if (idPropertyGenericType instanceof TypeVariable) {
            logger.debug("javersType of {} inferred as TokenType", idPropertyGenericType);
            return new TokenType((TypeVariable) idPropertyGenericType);
        }
        logger.debug("javersType of {} inferred as ValueType, it's used as id-property type",
                idPropertyGenericType);
        return new ValueType(idPropertyGenericType);
    }

    private JaversType spawnFromPrototype(JavaRichType javaRichType, JaversType prototype) {
        Validate.argumentsAreNotNull(javaRichType, prototype);

        if (prototype instanceof ManagedType) {
            ManagedType managedPrototype = (ManagedType) prototype;

            ManagedClass managedClass = managedClassFactory.createFromPrototype(javaRichType.javaClass, javaRichType.getScan(),
                    managedPrototype.getManagedClass().getManagedPropertiesFilter());
            return managedPrototype.spawn(managedClass, javaRichType.getScan().typeName());
        } else {
            return prototype.spawn(javaRichType.javaType); //delegate to simple constructor
        }
    }

    private JaversType createDefaultType(JavaRichType t) {
        return create(valueObjectDefinition(t.javaClass)
                .withTypeName(t.getScan().typeName())
                .defaultType()
                .build(), t.getScan());
    }

    private Optional<JaversType> inferFromAnnotations(JavaRichType t) {
        if (t.getScan().hasValueAnn()) {
            return Optional.of(create(new ValueDefinition(t.javaClass), t.getScan()));
        }

        if (t.getScan().hasIgnoredAnn()) {
            return Optional.of(create(new IgnoredTypeDefinition(t.javaClass), t.getScan()));
        }

        if (t.getScan().hasValueObjectAnn()) {
            return Optional.of(create(valueObjectDefinition(t.javaClass).withTypeName(t.getAnnTypeName()).build(),t.getScan()));
        }

        if (t.getScan().hasShallowReferenceAnn()) {
            return Optional.of(create(entityDefinition(t.javaClass).withTypeName(t.getAnnTypeName()).withShallowReference().build(), t.getScan()));
        }

        if (t.getScan().hasEntityAnn() || t.getScan().hasIdProperty()) {
            return Optional.of(create(entityDefinition(t.javaClass).withTypeName(t.getAnnTypeName()).build(), t.getScan()));
        }

        return Optional.empty();
    }

    private class JavaRichType {
        private Type javaType;
        private Class javaClass;
        private ClassScan scan;
        Supplier<ClassScan> classScan;

        JavaRichType(Type javaType) {
            this.javaType = javaType;
            this.javaClass = extractClass(javaType);
            this.classScan = () -> classScanner.scan(javaClass);
        }

        Object getSimpleName() {
            return javaClass.getSimpleName();
        }

        ClassScan getScan() {
            if (scan == null) {
                scan = classScan.get();
            }
            return scan;
        }

        Optional<String> getAnnTypeName() {
            return getScan().typeName();
        }
    }
}
