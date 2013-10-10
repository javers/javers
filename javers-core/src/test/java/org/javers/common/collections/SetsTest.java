package org.javers.common.collections;

import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

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
}
