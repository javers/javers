package org.javers;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.CdoSnapshot;

import javax.persistence.Id;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Javers javers = JaversBuilder.javers().build();
        MyEntity entity = new MyEntity(1, "some value");

        //initial commit
        javers.commit("author", entity);

        //some change
        entity.setValue("another value");

        //commit after change
        javers.commit("author", entity);

        //get state history
        List<Change> stateHistory = javers.getChangeHistory(1, MyEntity.class, 100);
        System.out.println("Changes count: " + stateHistory.size());

        //snapshot after initial commit
        ValueChange change = (ValueChange) stateHistory.get(0);
        System.out.println("Property value before change: " + change.getLeft());
        System.out.println("Property value after change: " + change.getRight());
    }


    private static class MyEntity {

        @Id
        private int id;
        private String value;

        private MyEntity(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
