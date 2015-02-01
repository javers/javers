package org.javers.core.cases;

import javax.persistence.Id;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class EntityWithNestedList {

    @Id
    private Integer id;
    private List<String> tags;
    private List<List<String>> nestedList;
    private List<ThreadLocal<String>> listWithGenericItem;

    public List<List<String>> getNestedList() {
        return nestedList;
    }

    public void setNestedList(List<List<String>> nestedList) {
        this.nestedList = nestedList;
    }

    public List<ThreadLocal<String>> getListWithGenericItem() {
        return listWithGenericItem;
    }

    public void setListWithGenericItem(List<ThreadLocal<String>> listWithGenericItem) {
        this.listWithGenericItem = listWithGenericItem;
    }
}
