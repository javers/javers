/*
* Copyright (c) 2017 Brevan Howard Limited. All rights reserved.
*/
package org.javers.core.cases;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.MappingStyle;
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
        final Javers javers = JaversBuilder.javers().withMappingStyle(MappingStyle.BEAN).registerEntity(Animal.class).build();
        final Animal cat = Animal.builder().setName("Cat").setNumberOfLegs(4).build();
        final Animal amputee = Animal.builder().setName("Cat").setNumberOfLegs(3).build();
        final Diff diff = javers.compare(cat, amputee);
        assertThat(diff.getChanges(), hasSize(1));
    }

    @Test
    public void commitAnAutoValueClass() {
        final Javers javers = JaversBuilder.javers().withMappingStyle(MappingStyle.BEAN).registerEntity(Animal.class).build();
        final Commit cat = javers.commit("Alice", Animal.builder().setName("Cat").setNumberOfLegs(4).build());
        assertThat(cat.getAuthor(), is("Alice"));
        final Commit amputee = javers.commit("Alice", Animal.builder().setName("Cat").setNumberOfLegs(3).build());
        assertThat(amputee.getAuthor(), is("Alice"));
        assertThat(amputee.getChanges(), hasSize(1));
    }
}