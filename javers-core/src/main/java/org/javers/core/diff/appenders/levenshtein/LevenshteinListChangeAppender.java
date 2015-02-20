package org.javers.core.diff.appenders.levenshtein;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author kornel kiełczewski
 */
class LevenshteinListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private static final Logger logger = LoggerFactory.getLogger(LevenshteinListChangeAppender.class);

    private final Backtrack backtrack = new Backtrack();

    private final StepsToChanges stepsToChanges = new StepsToChanges();

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(final NodePair pair, final Property property) {

        final List leftList = (List) pair.getLeftPropertyValue(property);
        final List rightList = (List) pair.getRightPropertyValue(property);

        final BacktrackSteps[][] steps = backtrack.evaluateSteps(leftList, rightList);
        final List<ContainerElementChange> changes = stepsToChanges.convert(steps, leftList, rightList);

        return getListChange(pair.getGlobalId(), property, changes);
    }

    private ListChange getListChange(final GlobalId affectedCdoId, final Property property,
                                     final List<ContainerElementChange> changes) {
        final ListChange result;

        if (changes.size() == 0) {
            result = null;
        } else {
            result = new ListChange(affectedCdoId, property, changes);
        }
        return result;
    }

}
