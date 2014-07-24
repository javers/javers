package org.javers.core.diff;

import org.javers.common.collections.Optional;
import org.javers.common.patterns.visitors.Visitable;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diff is a set of (atomic) changes between two graphs of objects.
 * <br><br>
 *
 * Typically it is used to capture and trace changes made by user on his domain data.
 * In this case diff is done between previous and current state of a bunch of domain objects.
 * <br><br>
 *
 * @author bartosz walacik
 */
public class Diff implements Visitable<ChangeVisitor>{
    private final List<Change> changes;

    Diff(List<Change> changes) {
        this.changes = changes;
    }

    /**
     * @return unmodifiable list
     */
    public List<Change> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    public boolean hasChanges() {
        return !changes.isEmpty();
    }

    @Override
    public void accept(ChangeVisitor changeVisitor) {
        for(Change change : changes) {
            change.accept(changeVisitor);
        }
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder();

        b.append("changes - ");
        for (Map.Entry<Class<? extends Change>, Integer> e : countByType().entrySet()){
            b.append(e.getKey().getSimpleName()+ ":"+e.getValue()+" ");
        }
        return b.toString().trim();
    }

    public Map<Class<? extends Change>, Integer> countByType(){
        Map<Class<? extends Change>, Integer> result = new HashMap<>();
        for(Change change : changes) {
            Class<? extends Change> key = change.getClass();
            if (result.containsKey(change.getClass())){
                result.put(key, (result.get(key))+1);
            }else{
                result.put(key, 1);
            }
        }
        return result;
    }
}
