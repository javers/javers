/*
* Copyright (c) 2017 Brevan Howard Limited. All rights reserved.
*/
package org.javers.core.cases;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.commit.Commit;
import org.javers.core.diff.Diff;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author jfreedman
 */
public class AutoValueCase {
    @Test
    public void diffAnAutoValueClass() {
        final Javers javers = JaversBuilder.javers().build();
        final Animal cat = Animal.builder().setName("Cat").setNumberOfLegs(4).build();
        final Animal spider = Animal.builder().setName("Spider").setNumberOfLegs(8).build();
        final Diff diff = javers.compare(cat, spider);
        assertThat(diff.getChanges(), hasSize(2));
    }

    @Test
    public void commitAnAutoValueClass() {
        final Javers javers = JaversBuilder.javers().build();
        final Animal cat = Animal.builder().setName("Cat").setNumberOfLegs(4).build();
        final Commit commit = javers.commit("Alice", cat);
        assertThat(commit.getAuthor(), is("Alice"));
    }
}