package org.javers.core.diff.appenders.levenshtein;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListType;

import java.util.List;

/**
 * @author kornel kiełczewski
 */
public class LevenshteinListChangeAppender extends CorePropertyChangeAppender<ListChange> {

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
