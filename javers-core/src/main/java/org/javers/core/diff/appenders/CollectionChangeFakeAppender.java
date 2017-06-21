package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;

/**
 * @author bartosz.walacik
 */
public class CollectionChangeFakeAppender extends CorePropertyChangeAppender<PropertyChange> {
    private static final Logger logger = LoggerFactory.getLogger(CollectionChangeFakeAppender.class);

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType.getClass() == CollectionType.class;
    }

    @Override
    public PropertyChange calculateChanges(NodePair pair, JaversProperty property) {

        Collection leftCol = (Collection) pair.getLeftPropertyValue(property);
        Collection rightCol = (Collection) pair.getRightPropertyValue(property);

        if (!Objects.equals(leftCol, rightCol)) {
            logger.warn("Collections: " + property + "\n"+
                        "are not equals but can't be compared. " +
                        "Raw Collection properties are not supported. Expected Set, List or any of their subclasses. "+
                        "JaVers uses different algorithms for comparing Sets and Lists and needs to know (statically) which one to test.");
        }

        return null;
    }

    @Override
    public int priority() {
        return super.priority();
    }
}
