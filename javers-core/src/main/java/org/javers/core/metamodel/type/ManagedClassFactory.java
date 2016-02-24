package org.javers.core.metamodel.type;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Predicate;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.scanner.ClassScan;
import org.javers.core.metamodel.scanner.ClassScanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author bartosz walacik
 */
class ManagedClassFactory {
    private final ClassScanner classScanner;
    private final TypeMapper typeMapper;

    public ManagedClassFactory(ClassScanner classScanner, TypeMapper typeMapper) {
        this.classScanner = classScanner;
        this.typeMapper = typeMapper;
    }

    ManagedClass create(Class<?> baseJavaClass){
        ClassScan scan = classScanner.scan(baseJavaClass);
        return new ManagedClass(baseJavaClass, scan.getProperties(), scan.getLooksLikeId());
    }

    ManagedClass create(ClientsClassDefinition def){
        ClassScan scan = classScanner.scan(def.getBaseJavaClass());
        List<Property> filtered = filterIgnored(scan.getProperties(), def);
        filtered = filterIgnoredType(filtered, def.getBaseJavaClass());
        return new ManagedClass(def.getBaseJavaClass(), filtered, scan.getLooksLikeId());
    }

    ManagedClass createShallowReferenceManagedClass(EntityDefinition def){
        ClassScan scan = classScanner.scan(def.getBaseJavaClass());
        return new ManagedClass(def.getBaseJavaClass(), Collections.<Property>emptyList(), scan.getLooksLikeId());
    }

    private List<Property> filterIgnoredType(List<Property> properties, final Class<?> currentClass){
        return Lists.negativeFilter(properties, new Predicate<Property>() {
            public boolean apply(Property property) {
                if (property.getRawType() == currentClass){
                    return false;
                }
                //prevents stackoverflow
                if (typeMapper.contains(property.getRawType()) ||
                    typeMapper.contains(property.getGenericType())) {
                    return typeMapper.getJaversType(property.getRawType()) instanceof IgnoredType;
                }

                return ReflectionUtil.isAnnotationPresentInHierarchy(property.getRawType(), DiffIgnore.class);
            }
        });
    }

    private List<Property> filterIgnored(List<Property> properties, ClientsClassDefinition definition){
        if (definition.getIgnoredProperties().isEmpty()){
            return properties;
        }

        List<Property> filtered = new ArrayList<>(properties);
        for (String ignored : definition.getIgnoredProperties()){
            filterOneProperty(filtered, ignored, definition.getBaseJavaClass());
        }
        return filtered;
    }

    private void filterOneProperty(List<Property> properties, String ignoredName, Class<?> clientsClass) {
        Iterator<Property> it = properties.iterator();
        while (it.hasNext()) {
            Property property = it.next();
            if (property.getName().equals(ignoredName)) {
                it.remove();
                return;
            }
        }
        throw new JaversException(JaversExceptionCode.PROPERTY_NOT_FOUND, ignoredName, clientsClass.getName());
    }
}
