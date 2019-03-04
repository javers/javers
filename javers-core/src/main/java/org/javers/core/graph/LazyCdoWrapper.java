package org.javers.core.graph;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.ManagedType;

import java.util.function.Supplier;

class LazyCdoWrapper extends LiveCdo {
    private final Supplier<?> cdoSupplier;

    LazyCdoWrapper(Supplier<?> cdoSupplier, GlobalId globalId, ManagedType managedType) {
        super(globalId, managedType);
        this.cdoSupplier = cdoSupplier;
    }

    @Override
    Object wrappedCdo() {
        return cdoSupplier.get();
    }
}
