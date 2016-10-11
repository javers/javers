package org.javers.guava;

import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.metamodel.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

import static org.javers.core.metamodel.type.JaversType.DEFAULT_TYPE_PARAMETER;

/**
 * @author akrystian
 */
public class GuavaCollectionsComparator{
    private static final Logger logger = LoggerFactory.getLogger(GuavaCollectionsComparator.class);
    /**
     * JaVers needs to know actual Class of elements stored in your Collections and Maps. <br/>
     * Wildcards (e.g. Set&lt;?&gt;), unbounded type parameters (e.g. Set&lt;T&gt;) <br/>
     * or missing parameters (e.g. Set) are defaulted to Object.class.
     * <br/><br/>
     * For Collections of Values it's a reasonable guess <br/>
     * but for Collections of Entities or ValueObjects you should use fully parametrized types (e.g. Set&lt;Person&gt;).
     */
    public static final String GENERIC_TYPE_NOT_PARAMETRIZED = "GENERIC_TYPE_NOT_PARAMETRIZED";

    protected void renderNotParametrizedWarningIfNeeded(Type parameterType, String parameterName, String colType, Property property){
        if (parameterType == DEFAULT_TYPE_PARAMETER){
            printNotParametrizedWarning(parameterName, colType, property);
        }
    }

    private void printNotParametrizedWarning(String parameterName, String colType, Property property) {
        logger.warn("Unknown {} type in {} property: {}. Defaulting to {}, see {}.{}",
                parameterName,
                colType,
                property.toString(),
                DEFAULT_TYPE_PARAMETER.getSimpleName(),
                CorePropertyChangeAppender.class.getSimpleName(),
                GENERIC_TYPE_NOT_PARAMETRIZED);
    }
}
