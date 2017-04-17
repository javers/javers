package org.javers.core.examples;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.examples.model.Person;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.javers.snapshotscompiler.JaversSnapshotsCompiler;

import java.util.ArrayList;
import java.util.List;

public class SnapshotsCompiler {
    public static void main(String[] args){

        Javers javers = JaversBuilder.javers().build();

        Person tommyOld = new Person("tommy", "Tommy Smart");
        javers.commit("tommy", tommyOld);

        Person tommyNew = new Person("tommy", "Tommy C. Smart");
        javers.commit("tommy", tommyNew);
        List<CdoSnapshot> snapshots = javers.findSnapshots(QueryBuilder.byInstanceId("tommy", Person.class).build());
        JaversSnapshotsCompiler snapCompiler = new JaversSnapshotsCompiler(javers);
        CdoSnapshot first = snapshots.get(snapshots.size()-1);
        CdoSnapshot last = snapshots.get(0);

        Person tommy0 = (Person) snapCompiler.compileEntityStateFromSnapshot(first);
        Person tommy1 = (Person) snapCompiler.compileEntityStateFromSnapshot(last);

        System.out.println("Test finished!");
    }
}
