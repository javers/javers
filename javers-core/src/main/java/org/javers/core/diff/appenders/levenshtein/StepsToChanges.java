package org.javers.core.diff.appenders.levenshtein;

import org.javers.core.diff.EqualsFunction;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ElementValueChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;

import java.util.ArrayList;
import java.util.List;

class StepsToChanges {

    private final EqualsFunction equalsFunction;

    public StepsToChanges(EqualsFunction equalsFunction) {
        this.equalsFunction = equalsFunction;
    }

    List<ContainerElementChange> convert(final BacktrackSteps[][] backtrack, final List leftList, final List rightList) {
        int i = leftList.size();
        int j = rightList.size();

        final List<ContainerElementChange> changes = new ArrayList<>();

        while (i * j != 0) {
            final BacktrackSteps choice = backtrack[i][j];
            final Object leftValue = leftList.get(i - 1);
            final Object rightValue = rightList.get(j - 1);

            if (choice == BacktrackSteps.TAKE) {
                if (!equalsFunction.nullSafeEquals(leftValue, rightValue)) {
                    changes.add(new ElementValueChange(i - 1, leftValue, rightValue));
                }
                i--;
                j--;
            } else if (choice == BacktrackSteps.SKIP_LEFT) {
                changes.add(new ValueRemoved(i - 1, leftValue));
                i--;
            } else if (choice == BacktrackSteps.SKIP_RIGHT) {
                changes.add(new ValueAdded(j - 1, rightValue));
                j--;
            }
        }

        while(i > 0) {
            changes.add(new ValueRemoved(i - 1, leftList.get(i - 1)));
            i--;
        }

        while(j > 0) {
            changes.add(new ValueAdded(j - 1, rightList.get(j - 1)));
            j--;
        }

        return changes;
    }
}
