package org.javers.model.mapping;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pawel Szymczyk
 */
public class ManagedClasses {

    private List<ManagedClass> list;

    protected ManagedClasses() {
        list = new ArrayList<ManagedClass>();
    }

    protected int count() {
        return list.size();
    }

    protected void add(ManagedClass managedClass) {
        list.add(managedClass);
    }

    protected boolean contains(ManagedClass managedClass) {
        return list.contains(managedClass);
    }

    protected boolean containsManagedClassWithSourceClass(Class sourceClass) {
        return getBySourceClass(sourceClass) != null;
    }

    protected ManagedClass getBySourceClass(Class sourceClass) {
        for (ManagedClass managedClass: list) {
            if (managedClass.getSourceClass().equals(sourceClass)) return managedClass;
        }
        return null;
    }

}
