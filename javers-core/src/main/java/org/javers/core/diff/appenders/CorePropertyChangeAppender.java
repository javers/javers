package org.javers.core.diff.appenders;

import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

import static org.javers.core.metamodel.type.JaversType.DEFAULT_TYPE_PARAMETER;

/**
 * @author bartosz walacik
 */
public abstract class CorePropertyChangeAppender<T extends PropertyChange> implements PropertyChangeAppender<T> {
    private static final Logger logger = LoggerFactory.getLogger(CorePropertyChangeAppender.class);

    @Override
    public int priority() {
        return LOW_PRIORITY;
    }

    protected void renderNotParametrizedWarningIfNeeded(Type parameterType, String parameterName, String colType, Property property){
        if (parameterType == DEFAULT_TYPE_PARAMETER){
            printNotParametrizedWarning(parameterName, colType, property);
        }
    }

    private void printNotParametrizedWarning(String parameterName, String colType, Property property) {
        logger.warn("Unknown {} type in {} property: {}. Defaulting to {}, see JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED",
                parameterName,
                colType,
                property.toString(),
                DEFAULT_TYPE_PARAMETER.getSimpleName());
    }
}
