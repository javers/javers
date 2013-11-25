package org.javers.common.collections;

import java.util.Arrays;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SetsTest {

    @Test
    public void shouldCalculateDifference() {
        // given:
        Set<String> first = Sets.asSet("a", "b", "c");
        Set<String> second = Sets.asSet("b", "c", "d");

        // when:
        Set<String> difference = Sets.difference(first, second);

        // then:
        Assert.assertEquals(difference.size(), 1);
        Assert.assertTrue(difference.contains("a"));
    }

    @Test
    public void shouldNotChangeArgumentsWhenCalculatingDifference() {
        // given:
        Set<String> first = Sets.asSet("a", "b", "c");
        Set<String> second = Sets.asSet("b", "c", "d");

        // when:
        Sets.difference(first, second);

        // then:
        Assert.assertEquals(first.size(), 3);
        Assert.assertTrue(first.containsAll(Arrays.asList("a", "b", "c")));
        Assert.assertEquals(second.size(), 3);
        Assert.assertTrue(second.containsAll(Arrays.asList("b", "c", "d")));
    }
}
