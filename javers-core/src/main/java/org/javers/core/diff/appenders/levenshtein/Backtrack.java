package org.javers.core.diff.appenders.levenshtein;

import org.javers.common.collections.Objects;

import java.util.List;


//TODO: We have a heavy use of list.get, which relies on both lists being quickly randomly accessible, like an ArrayList. Is that assumption ok?
class Backtrack {

    private final static int PENALTY = 1;

    BacktrackSteps[][] evaluateSteps(final List leftList, final List rightList) {

        final int leftDim = leftList.size() + 1;
        final int rightDim = rightList.size() + 1;

        final BacktrackSteps[][] steps = new BacktrackSteps[leftDim][rightDim];

        final int[][] scores = initScores(leftDim, rightDim);

        for (int i = 1; i < leftDim; ++i) {
            for (int j = 1; j < rightDim; ++j) {
                int skipLeft = scores[i - 1][j] - PENALTY;
                int skipRight = scores[i][j - 1] - PENALTY;
                int take = scores[i - 1][j - 1] - compareListElements(leftList.get(i - 1), rightList.get(j - 1));
                int max = Math.max(skipLeft, Math.max(skipRight, take));

                final BacktrackSteps step;

                if (max == skipLeft) {
                    step = BacktrackSteps.SKIP_LEFT;
                } else if (max == skipRight) {
                    step = BacktrackSteps.SKIP_RIGHT;
                } else {
                    step = BacktrackSteps.TAKE;
                }

                scores[i][j] = max;
                steps[i][j] = step;
            }
        }
        return steps;
    }

    private int[][] initScores(final int leftDim, final int rightDim) {
        final int[][] s = new int[leftDim][rightDim];
        for (int i = 0; i < leftDim; ++i) {
            s[i][0] = -i * PENALTY;
        }

        for (int j = 0; j < rightDim; ++j) {
            s[0][j] = -j * PENALTY;
        }
        return s;
    }

    private int compareListElements(final Object left, final Object right) {
        if (Objects.nullSafeEquals(left, right)) {
            return 0;
        } else {
            return PENALTY;
        }
    }

}
