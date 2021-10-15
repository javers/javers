package org.javers.core.diff.appenders.levenshtein;

import org.javers.core.diff.EqualsFunction;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListType;

import java.util.List;

/**
 * @author kornel kielczewski
 */
public class LevenshteinListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, JaversProperty property) {
        JaversType itemType = ((ListType)property.getType()).getItemJaversType();

        final List leftList =  (List) leftValue;
        final List rightList = (List) rightValue;

        EqualsFunction equalsFunction = itemType::equals;
        Backtrack backtrack = new Backtrack(equalsFunction);
        StepsToChanges stepsToChanges = new StepsToChanges(equalsFunction);

        final BacktrackSteps[][] steps = backtrack.evaluateSteps(leftList, rightList);
        final List<ContainerElementChange> changes = stepsToChanges.convert(steps, leftList, rightList);

        ListChange result = createListChange(pair, property, changes, leftList, rightList);
        if (result != null) {
            renderNotParametrizedWarningIfNeeded(itemType.getBaseJavaType(), "item", "List", property);
        }
        return result;
    }

    private ListChange createListChange(NodePair pair, JaversProperty property, List<ContainerElementChange> changes, List left, List right) {
        final ListChange result;

        if (changes.isEmpty()) {
            result = null;
        } else {
            result = new ListChange(pair.createPropertyChangeMetadata(property), changes, left, right);
        }
        return result;
    }
}
