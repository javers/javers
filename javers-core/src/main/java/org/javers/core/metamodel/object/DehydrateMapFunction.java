package org.javers.core.metamodel.object;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.MapContentType;
import org.javers.core.metamodel.type.MapEnumerationOwnerContext;

/**
* @author bartosz walacik
*/
public class DehydrateMapFunction implements EnumerableFunction {
    private final GlobalIdFactory globalIdFactory;
    private final MapContentType mapContentType;

    public DehydrateMapFunction(GlobalIdFactory globalIdFactory, MapContentType mapContentType) {
        Validate.argumentsAreNotNull(globalIdFactory, mapContentType);
        this.globalIdFactory = globalIdFactory;
        this.mapContentType = mapContentType;
    }

    @Override
    public Object apply(Object input, EnumerationAwareOwnerContext context) {
        MapEnumerationOwnerContext mapContext = (MapEnumerationOwnerContext)context;
        if (mapContext.isKey()){
            return globalIdFactory.dehydrate(input, mapContentType.getKeyType(), context);
        }
        else {
            return globalIdFactory.dehydrate(input, mapContentType.getValueType(), context);
        }
    }
}
