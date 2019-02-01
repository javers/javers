package org.javers.core.cases;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.Id;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.annotation.ValueObject;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.junit.Test;

/**
 * @author adriano.machado
 */
public class CaseXXXSortedSets {

    public static class MasterWithSet {
        @Id
        String id;

        Set<Detail> set;
    }

    public static class MasterWithSortedSet {
        @Id
        String id;

        SortedSet<Detail> sortedSet;
    }

    @ValueObject
    public static class Detail {
        String data;

        Detail(String data) {
            this.data = data;
        }
    }

    @Test
    public void shouldWorkWithObjectsUsingSets() {
        //given
        Javers javers = JaversBuilder.javers().build();

        MasterWithSet master = new MasterWithSet();
        master.id = "X";
        master.set = new HashSet<>();

        master.set.add(new Detail("data 2"));
        master.set.add(new Detail("data 1"));

        javers.commit("anonymous", master);
        javers.commit("anonymous", master);

        List<CdoSnapshot> snapshots = javers.findSnapshots(QueryBuilder.byClass(CaseXXXSortedSets.MasterWithSet.class).build());

        assertThat(snapshots).isNotEmpty();
    }

    @Test
    public void shouldWorkWithObjectsUsingSortedSets() {
        //given
        Javers javers = JaversBuilder.javers().build();

        MasterWithSortedSet master = new MasterWithSortedSet();
        master.id = "X";
        master.sortedSet = new TreeSet<>(Comparator.comparing(o -> o.data));

        master.sortedSet.add(new Detail("data 2"));
        master.sortedSet.add(new Detail("data 1"));

        javers.commit("anonymous", master);
        javers.commit("anonymous", master);

        List<CdoSnapshot> snapshots = javers.findSnapshots(QueryBuilder.byClass(CaseXXXSortedSets.MasterWithSortedSet.class).build());

        assertThat(snapshots).isNotEmpty();
    }
}
