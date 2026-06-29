package org.javers.core.cases;

import java.util.List;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <a href="https://github.com/javers/javers/issues/1490">Javers issues 1490</a>
 *
 * @author Adrien-dev25
 */
public class Case1490CompareNestedListWithAlgorithmAsSetTest {

    private final static Javers JAVERS = JaversBuilder.javers()
                                                      .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
                                                      .build();

    @Test
    public void compareNestedListsWithDifferentOrderUsingAsSetAlgorithm() {
        // GIVEN two nested Lists containing same elements but in a different order
        List<List<String>> streetsOriginalOrder = List.of(List.of("Street A", "Street B"));
        List<List<String>> streetsReversedOrder = List.of(List.of("Street B", "Street A"));

        // WHEN compare with javers setup with ListCompareAlgorithm.AS_SET
        Diff diff = JAVERS.compare(streetsOriginalOrder, streetsReversedOrder);

        // THEN we expect no change to be detected
        Assertions.assertFalse(diff.hasChanges());
    }

}
