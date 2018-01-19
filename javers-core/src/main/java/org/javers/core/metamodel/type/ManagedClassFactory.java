package org.javers.core.metamodel.type;

import org.javers.common.collections.Lists;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.scanner.ClassScan;

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

    ManagedClass create(ClientsClassDefinition def, ClassScan scan) {
        List<JaversProperty> allProperties = convert(scan.getProperties());

        ManagedPropertiesFilter managedPropertiesFilter =
                new ManagedPropertiesFilter(def.getBaseJavaClass(), allProperties, def.getPropertiesFilter());

        return create(def.getBaseJavaClass(), allProperties, managedPropertiesFilter);
    }

    ManagedClass createFromPrototype(Class<?> baseJavaClass, ClassScan scan, ManagedPropertiesFilter prototypePropertiesFilter) {
        List<JaversProperty> allProperties = convert(scan.getProperties());
        return create(baseJavaClass, allProperties, prototypePropertiesFilter);
    }

    private ManagedClass create(Class<?> baseJavaClass, List<JaversProperty> allProperties, ManagedPropertiesFilter propertiesFilter){

        List<JaversProperty> filtered = propertiesFilter.filterProperties(allProperties);

        filtered = filterIgnoredType(filtered, baseJavaClass);

        return new ManagedClass(baseJavaClass, filtered,
                positiveFilter(allProperties, p -> p.looksLikeId()), propertiesFilter);
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

        return Lists.negativeFilter(properties, property -> {
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
}
