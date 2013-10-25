package org.javers.model.mapping;

import java.util.ArrayList;
import java.util.List;

public class ManagedClasses {

    private List<ManagedClass> list;

    public ManagedClasses() {
        list = new ArrayList<ManagedClass>();
    }

    public void add(ManagedClass managedClass) {
        list.add(managedClass);
    }

    public boolean contains(ManagedClass managedClass) {
        return list.contains(managedClass);
    }

    public boolean containsManagedClassWithSourceClass(Class sourceClass) {
        return getBySourceClass(sourceClass) != null;
    }

    public ManagedClass getBySourceClass(Class sourceClass) {
        for (ManagedClass managedClass: list) {
            if (managedClass.getSourceClass().equals(sourceClass)) return managedClass;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManagedClasses that = (ManagedClasses) o;

        if (!list.equals(that.list)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }
}
