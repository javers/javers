package org.javers.core.metamodel.object;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.type.JaversType;

/**
* @author bartosz walacik
*/
public class DehydrateContainerFunction implements EnumerableFunction {
    private final JaversType itemType;
    private final GlobalIdFactory globalIdFactory;

    public DehydrateContainerFunction(JaversType itemType, GlobalIdFactory globalIdFactory) {
        this.itemType = itemType;
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public Object apply(Object input, EnumerationAwareOwnerContext ownerContext) {
        return globalIdFactory.dehydrate(input, itemType, ownerContext);
    }
}
