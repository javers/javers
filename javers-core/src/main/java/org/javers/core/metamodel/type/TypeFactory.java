package org.javers.core.metamodel.type;

import java.lang.reflect.TypeVariable;
import java.util.*;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.*;
import org.javers.core.metamodel.scanner.ClassScan;
import org.javers.core.metamodel.scanner.ClassScanner;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.javers.core.metamodel.clazz.EntityDefinitionBuilder.entityDefinition;
import static org.javers.core.metamodel.clazz.ValueObjectDefinitionBuilder.valueObjectDefinition;

/**
 * @author bartosz walacik
 */
class TypeFactory {
    private static final Logger logger = TypeMapper.logger;

    private final Map<Type, Hint> votes = new ConcurrentHashMap<>();

    private final ClassScanner classScanner;
    private final ManagedClassFactory managedClassFactory;
    private final EntityTypeFactory entityTypeFactory;

    private final DynamicMappingStrategy dynamicMappingStrategy;

    TypeFactory(ClassScanner classScanner, TypeMapper typeMapper, DynamicMappingStrategy dynamicMappingStrategy) {
        this.classScanner = classScanner;

        this.dynamicMappingStrategy = dynamicMappingStrategy;

        //Pico doesn't support cycles, so manual construction
        this.managedClassFactory = new ManagedClassFactory(typeMapper);

        this.entityTypeFactory = new EntityTypeFactory(managedClassFactory);
    }

    JaversType create(ClientsClassDefinition def) {
        return create(def, null);
    }

    JaversType create(ClientsClassDefinition def, ClassScan scanMaybe) {
        Supplier<ClassScan> lazyScan = () -> scanMaybe != null ? scanMaybe : classScanner.scan(def.getBaseJavaClass());

        if (def instanceof CustomDefinition) {
            return new CustomType(def.getBaseJavaClass(), ((CustomDefinition) def).getComparator());
        } else if (def instanceof EntityDefinition) {
            EntityType newType = entityTypeFactory.createEntity((EntityDefinition) def, lazyScan.get());
            saveHints(newType);
            return newType;
        } else if (def instanceof ValueObjectDefinition) {
            return createValueObject((ValueObjectDefinition) def, lazyScan.get());
        } else if (def instanceof ValueDefinition) {
            ValueDefinition valueDefinition = (ValueDefinition) def;
            return new ValueType(valueDefinition.getBaseJavaClass(),
                    valueDefinition.getComparator());
        } else if (def instanceof IgnoredTypeDefinition) {
            return new IgnoredType(def.getBaseJavaClass());
        } else {
            throw new IllegalArgumentException("unsupported definition " + def.getClass().getSimpleName());
        }
    }

    private void saveHints(EntityType newEntityType) {
        if (!newEntityType.hasCompositeId()) {
            votes.put(newEntityType.getIdProperty().getGenericType(), new EntityIdHint());
        }
    }

    private ValueObjectType createValueObject(ValueObjectDefinition definition, ClassScan scan) {
        return new ValueObjectType(managedClassFactory.create(definition, scan), definition.getTypeName(), definition.isDefault());
    }

    /**
     * for tests only
     */
    private JaversType infer(Type javaType) {
        return infer(javaType, Optional.empty());
    }

    JaversType infer(Type javaType, Optional<JaversType> prototype) {

        Optional<JaversType> tokenType = resolveIfTokenType(javaType);
        if (tokenType.isPresent()) {
            return tokenType.get();
        }

        final JavaRichType javaRichType = new JavaRichType(javaType);

        if (prototype.isPresent()) {
            JaversType jType = spawnFromPrototype(javaRichType, prototype.get());
            logger.debug("registering prototype-based type: {} -> {}, inferred from prototype {}",
                    javaType.getTypeName(), jType.getClass().getSimpleName(), prototype.get());
            return jType;
        }

        Optional<JaversType> dynamicType = dynamicMappingStrategy.map(javaType);

        return dynamicType
                .orElseGet(() -> inferFromAnnotations(javaRichType).map(jType -> {
                        logger.debug("registering dynamicType: {} -> {}, inferred from annotations",
                                javaType.getTypeName(), jType.getClass().getSimpleName());
                        return jType;
                })
                .orElseGet(() -> inferFromHints(javaRichType)
                .orElseGet(() -> createDefaultType(javaRichType))));
    }

    private Optional<JaversType> resolveIfTokenType(Type javaType) {
        if (javaType instanceof TypeVariable) {
            logger.debug("javersType for '{}' inferred as TokenType", javaType);
            return Optional.of(new TokenType((TypeVariable) javaType));
        }
        return Optional.empty();
    }

    private Optional<JaversType> inferFromHints(JavaRichType richType) {
        Hint vote = votes.get(richType.javaType);

        if (vote != null) {
            JaversType jType = vote.vote(richType);
            logger.debug("javersType for '{}' inferred as {}, based on {} ", richType.getTypeName(), jType.getClass().getSimpleName(), vote.getClass().getSimpleName());
            return Optional.of(jType);
        }

        return Optional.empty();
    }

    private JaversType spawnFromPrototype(JavaRichType javaRichType, JaversType prototype) {
        Validate.argumentsAreNotNull(javaRichType, prototype);

        if (prototype instanceof ManagedType) {
            ManagedType managedPrototype = (ManagedType) prototype;

            ManagedClass managedClass = managedClassFactory.createFromPrototype(javaRichType.javaClass, javaRichType.getScan(),
                    managedPrototype.getManagedClass().getManagedPropertiesFilter());
            return managedPrototype.spawn(managedClass, javaRichType.getScan().typeName());
        } else if (prototype instanceof CustomType) {
            CustomType customTypePrototype = (CustomType) prototype;
            return new CustomType(customTypePrototype.getBaseJavaType(), customTypePrototype.getComparator());
        } else {
            return prototype.spawn(javaRichType.javaType); //delegate to simple constructor
        }
    }

    private JaversType createDefaultType(JavaRichType t) {
        logger.debug("registering default type: {} -> {}",
                t.getTypeName(), ValueObjectType.class.getSimpleName());

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

        String getTypeName() {
            return javaType.toString();
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

    private interface Hint {
        JaversType vote(JavaRichType richType);
    }

    private static class EntityIdHint implements Hint {
        @Override
        public JaversType vote(JavaRichType richType) {
            return new ValueType(richType.javaType);
        }
    }
}
