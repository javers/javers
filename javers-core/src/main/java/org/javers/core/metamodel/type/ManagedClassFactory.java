package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.annotation.ClassAnnotationsScanner;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.PropertyScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author bartosz walacik
 */
class ManagedClassFactory {
    private static final Logger logger = LoggerFactory.getLogger(ManagedClassFactory.class);
    private final PropertyScanner propertyScanner;

    public ManagedClassFactory(PropertyScanner propertyScanner) {
        this.propertyScanner = propertyScanner;
    }

    ManagedClass create(Class<?> baseJavaClass){
        Validate.argumentIsNotNull(baseJavaClass);
        List<Property> properties = propertyScanner.scan(baseJavaClass);

        return new ManagedClass(baseJavaClass, properties);
    }

    ManagedClass create(ClientsClassDefinition def){
        Validate.argumentIsNotNull(def);
        List<Property> properties = propertyScanner.scan(def.getBaseJavaClass());
        List<Property> filteredProperties = filterIgnored(properties, def);

        return new ManagedClass(def.getBaseJavaClass(), filteredProperties);
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
