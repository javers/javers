package org.javers.core.metamodel.type;

import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.scanner.ClassScan;
import org.javers.core.metamodel.scanner.ClassScanner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.javers.common.collections.Lists.positiveFilter;

/**
 * @author bartosz walacik
 */
class ManagedClassFactory {
    private final TypeMapper typeMapper;

    public ManagedClassFactory(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    ManagedClass create(Class<?> baseJavaClass, ClassScan scan){
        List<JaversProperty> allProperties = convert(scan.getProperties());
        return new ManagedClass(baseJavaClass, allProperties,
                positiveFilter(allProperties, p -> p.looksLikeId()));
    }

    ManagedClass create(ClientsClassDefinition def, ClassScan scan){
        List<JaversProperty> allProperties = convert(scan.getProperties());

        List<JaversProperty> filtered = filterProperties(allProperties, def);

        filtered = filterIgnoredType(filtered, def.getBaseJavaClass());

        return new ManagedClass(def.getBaseJavaClass(), filtered,
                positiveFilter(allProperties, p -> p.looksLikeId()));
    }

    private List<JaversProperty> convert(List<Property> properties) {
        return Lists.transform(properties,  p -> {
            if (typeMapper.contains(p.getGenericType())) {
                final JaversType javersType = typeMapper.getJaversType(p.getGenericType());
                return new JaversProperty(() -> javersType, p);
            }
            return new JaversProperty(() -> typeMapper.getJaversType(p.getGenericType()), p);
        });
    }

    private List<JaversProperty> filterIgnoredType(List<JaversProperty> properties, final Class<?> currentClass){

        return (List)Lists.negativeFilter(properties, property -> {
            if (property.getRawType() == currentClass){
                return false;
            }
            //prevents stackoverflow
            if (typeMapper.contains(property.getRawType()) ||
                typeMapper.contains(property.getGenericType())) {
                return typeMapper.getJaversType(property.getRawType()) instanceof IgnoredType;
            }

            return ReflectionUtil.isAnnotationPresentInHierarchy(property.getRawType(), DiffIgnore.class);
        });
    }

    private List<JaversProperty> filterProperties(List<JaversProperty> allProperties, ClientsClassDefinition definition){
        if (definition.hasWhitelistedProperties()) {
            return onlyWhitelisted(allProperties, definition);
        }

        if (definition.hasIgnoredProperties()) {
            return filterIgnored(allProperties, definition);
        }

        return allProperties;
    }

    private List<JaversProperty> onlyWhitelisted(List<JaversProperty> allProperties, ClientsClassDefinition definition){
        List<JaversProperty> filtered = new ArrayList<>();
        for (String whiteName : definition.getWhitelistedProperties()) {
            JaversProperty whiteProperty = allProperties.stream()
                    .filter(p -> p.getName().equals(whiteName))
                    .findFirst()
                    .orElseThrow(() -> new JaversException(JaversExceptionCode.PROPERTY_NOT_FOUND, whiteName, definition.getBaseJavaClass().getName()));
            filtered.add(whiteProperty);
        }
        return filtered;
    }

    private List<JaversProperty> filterIgnored(List<JaversProperty> allProperties, ClientsClassDefinition definition){
        List<JaversProperty> filtered = new ArrayList<>(allProperties);
        for (String ignored : definition.getIgnoredProperties()){
            filterOneProperty(filtered, ignored, definition.getBaseJavaClass());
        }
        return filtered;
    }

    private void filterOneProperty(List<JaversProperty> properties, String ignoredName, Class<?> clientsClass) {
        Iterator<JaversProperty> it = properties.iterator();
        while (it.hasNext()) {
            JaversProperty property = it.next();
            if (property.getName().equals(ignoredName)) {
                it.remove();
                return;
            }
        }
        throw new JaversException(JaversExceptionCode.PROPERTY_NOT_FOUND, ignoredName, clientsClass.getName());
    }
}
