package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.PropertyScan;
import org.javers.core.metamodel.property.PropertyScanner;

import java.util.*;

/**
 * @author bartosz walacik
 */
class ManagedClassFactory {
    private final PropertyScanner propertyScanner;

    public ManagedClassFactory(PropertyScanner propertyScanner) {
        this.propertyScanner = propertyScanner;
    }

    ManagedClass create(Class<?> baseJavaClass){
        PropertyScan scan = propertyScanner.scan(baseJavaClass);
        return new ManagedClass(baseJavaClass, scan.getProperties(), scan.getLooksLikeId());
    }

    ManagedClass create(ClientsClassDefinition def){
        PropertyScan scan = propertyScanner.scan(def.getBaseJavaClass());
        List<Property> filtered = filterIgnored(scan.getProperties(), def);
        return new ManagedClass(def.getBaseJavaClass(), filtered, scan.getLooksLikeId());
    }

    ManagedClass createShallowReferenceManagedClass(EntityDefinition def){
        PropertyScan scan = propertyScanner.scan(def.getBaseJavaClass());
        return new ManagedClass(def.getBaseJavaClass(), Collections.<Property>emptyList(), scan.getLooksLikeId());
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
